package carp.covanalyser.application

import carp.covanalyser.application.events.*
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageAnalysisRepository
import dk.cachet.carp.common.application.UUID
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Manage coverage analysis
 */
//TODO should this be in application layer? Should I create repository classes to abstract storage of analyses and/or jobs away?
class DefaultCoverageAnalysisService(
    private var eventBus: EventBus,
    private var coverageAnalysisRepository: CoverageAnalysisRepository
) : CoverageAnalysisService {
    private val jobs = mutableMapOf<UUID, Job>()
    private val analysisScope = CoroutineScope(Dispatchers.Default)

    init {
        eventBus.subscribe(CoverageAnalysisRequestedEvent::class) {
            analysisScope.launch {
                handleCoverageAnalysisRequested(
                    it
                )
            }
        }
    }

    override suspend fun registerAnalysis(analysis: CoverageAnalysis) {
        coverageAnalysisRepository.add(analysis)
    }

    /**
     * If system clock is > than endTime --> full analysis
     * If system clock is < than startTime --> recurring analysis and wait for startTime
     * If system clock is > than startTime but < than endTime --> full analysis until system clock, then recurring analysis?
     */

    override suspend fun startAnalysis(id: UUID) {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")

        val currentTime = Clock.System.now()
        val job: Job = when {
            currentTime > analysis.endTime ->
                analyze(id, false, analysis.startTime, analysis.endTime)

            currentTime < analysis.startTime ->
                analyze(id, true, analysis.startTime, analysis.endTime)

            else -> analyzePartially(id)
        }
        jobs[id] = job
        analysisScope.launch {
            job.join()
            eventBus.publish(CoverageAnalysisCompletedEvent(id))
        }
    }

    private suspend fun analyzePartially(id: UUID): Job {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")
        return analysisScope.launch {
            val firstJob = analyze(id, false, analysis.startTime, Clock.System.now())
            firstJob.join()
            val secondJob = analyze(id, true, Clock.System.now(), analysis.endTime)
            secondJob.join()
        }
    }

    private suspend fun analyze(id: UUID, hasWaitTime: Boolean, startTime: Instant, endTime: Instant): Job {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")
        var currStartTime = startTime
        val job = analysisScope.launch {
            while (isActive && currStartTime < endTime) {
                //TODO what if endTime is not a multiple of timeBetweenCalculations?
                var currEndTime = currStartTime.plus(analysis.timeBetweenCalculations)
                analysis.calculateCoverage(currStartTime, currEndTime)
                eventBus.publish(CoverageCalculatedEvent(id))
                currStartTime = currEndTime
                if (hasWaitTime)
                    delay(analysis.timeBetweenCalculations) // delay is in milliseconds
            }
        }
        return job
    }

    override suspend fun stopAnalysis(id: UUID) {
        jobs[id]?.cancel()
    }

    override suspend fun stopAllAnalyses() {
        analysisScope.cancel()
    }

    private suspend fun handleCoverageAnalysisRequested(event: Event) {
        val coverageAnalysisRequestedEvent = event as CoverageAnalysisRequestedEvent
        println("Coverage analysis requested " + coverageAnalysisRequestedEvent.coverageAnalysis)
        registerAnalysis(
            coverageAnalysisRequestedEvent.coverageAnalysis
        )
        startAnalysis(coverageAnalysisRequestedEvent.id)
    }


}