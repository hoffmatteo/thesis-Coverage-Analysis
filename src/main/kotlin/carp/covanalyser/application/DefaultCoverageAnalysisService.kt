package carp.covanalyser.application

import carp.covanalyser.application.events.*
import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Manage coverage analysis
 */
class DefaultCoverageAnalysisService(
    private var eventBus: EventBus,
) : CoverageAnalysisService {
    //TODO how do I model  coverage of a single participant versus of an entire study?
    private val analyses = mutableMapOf<String, CoverageAnalysis>()
    private val jobs = mutableMapOf<String, Job>()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    init {
        eventBus.subscribe(CoverageAnalysisRequestedEvent::class) { handleCoverageAnalysisRequested(it) }
    }

    override fun registerAnalysis(id: String, analysis: CoverageAnalysis) {
        analyses[id] = analysis
    }

    /**
     * If system clock is > than endTime --> full analysis
     * If system clock is < than startTime --> recurring analysis and wait for startTime
     * If system clock is > than startTime but < than endTime --> full analysis until system clock, then recurring analysis?
     */

    override fun startAnalysis(id: String) {
        val analysis = analyses[id] ?: throw Exception("Analysis not found")

        val currentTime = Clock.System.now()
        val job: Job = when {
            currentTime > analysis.endTime ->
                analyze(id, false, analysis.startTime, analysis.endTime)

            currentTime < analysis.startTime ->
                analyze(id, true, analysis.startTime, analysis.endTime)

            else -> analyzePartially(id)
        }
        jobs[id] = job
        serviceScope.launch {
            job.join()
            eventBus.publish(CoverageAnalysisCompletedEvent(UUID.parse(id)))
        }
    }

    private fun analyzePartially(id: String): Job {
        val analysis = analyses[id] ?: throw Exception("Analysis not found")
        return serviceScope.launch {
            val firstJob = analyze(id, false, analysis.startTime, Clock.System.now())
            firstJob.join()
            val secondJob = analyze(id, true, Clock.System.now(), analysis.endTime)
            secondJob.join()
        }
    }

    @OptIn(ExperimentalTime::class)
    private fun analyze(id: String, hasWaitTime: Boolean, startTime: Instant, endTime: Instant): Job {
        val analysis = analyses[id] ?: throw Exception("Analysis not found")
        var currStartTime = startTime
        val job = serviceScope.launch {
            while (isActive && currStartTime < endTime) {
                var currEndTime = currStartTime.plus(analysis.timeBetweenCalculations)
                val coverage = analysis.calculateCoverage(currStartTime, currEndTime)
                eventBus.publish(CoverageCalculatedEvent(UUID.parse(id)))
                currStartTime = currEndTime
                if (hasWaitTime)
                    delay(analysis.timeBetweenCalculations) // delay is in milliseconds
            }
        }
        return job
    }

    override fun stopAnalysis(id: String) {
        jobs[id]?.cancel()
    }

    override fun stopAllAnalyses() {
        serviceScope.cancel()
    }

    private fun handleCoverageAnalysisRequested(event: Event) {
        val coverageAnalysisRequestedEvent = event as CoverageAnalysisRequestedEvent
        println("Coverage analysis requested " + coverageAnalysisRequestedEvent.coverageAnalysis)
        registerAnalysis(
            coverageAnalysisRequestedEvent.id.toString(),
            coverageAnalysisRequestedEvent.coverageAnalysis
        )
        startAnalysis(coverageAnalysisRequestedEvent.id.toString())
    }


}