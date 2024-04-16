package carp.covanalyser.core.application

import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.application.events.Event
import carp.covanalyser.core.application.events.EventBus
import carp.covanalyser.core.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Manage coverage analysis
 */
class DefaultCoverageAnalysisService(private var eventBus: EventBus) : CoverageAnalysisService {
    //TODO how do I model  coverage of a single participant versus of an entire study?
    private val analyses = mutableMapOf<String, CoverageAnalysis>()
    private val jobs = mutableMapOf<String, Job>()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    init {
        eventBus.subscribe(CoverageAnalysisRequestedEvent::class) { handleCoverageAnalysisRequested(it) }
    }

    //TODO listen to events and call functions based on that
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
            eventBus.publish(CoverageAnalysisCompletedEvent())
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

    // TODO call it schedule instead of delay?
    private fun analyze(id: String, hasDelay: Boolean, startTime: Instant, endTime: Instant): Job {
        val analysis = analyses[id] ?: throw Exception("Analysis not found")
        var currTime = startTime
        val job = serviceScope.launch {
            while (isActive && currTime < endTime) {
                println("$currTime")
                analysis.calculateCoverage(
                    startTime,
                    startTime.plus(analysis.timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                )
                currTime = currTime.plus(analysis.timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                if (hasDelay)
                    delay(analysis.timeFrameSeconds * 1000L) // delay is in milliseconds
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
        val id = UUID.randomUUID().toString()
        registerAnalysis(
            id,
            coverageAnalysisRequestedEvent.coverageAnalysis
        )
        startAnalysis(id)
    }


}