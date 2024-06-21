package carp.covanalyser.application

import carp.covanalyser.application.events.*
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageAnalysisRepository
import dk.cachet.carp.common.application.UUID
import kotlinx.coroutines.*
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

/**
 * Default implementation of [CoverageAnalysisService]. Uses [EventBus] and [CoverageAnalysisRepository].
 * @param eventBus The [EventBus] to publish and subscribe to events.
 * @param coverageAnalysisRepository The [CoverageAnalysisRepository] to manage [CoverageAnalysis] objects.
 * @property analysisScope The [CoroutineScope] that the analysis are run in.
 * @property jobs The [Job]s of the running analyses.
 */
class DefaultCoverageAnalysisService(
    override val eventBus: EventBus, private var coverageAnalysisRepository: CoverageAnalysisRepository,
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
        eventBus.subscribe(CoverageAnalysisStopRequestedEvent::class) {
            analysisScope.launch {
                handleCoverageAnalysisStopRequested(
                    it
                )
            }

        }
    }

    override suspend fun registerAnalysis(analysis: CoverageAnalysis) {
        coverageAnalysisRepository.add(analysis)
    }


    override suspend fun startAnalysis(id: UUID) {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")

        val currentTime = Clock.System.now()

        /**
         * If system clock is > than endTime --> full analysis
         * If system clock is < than startTime --> recurring analysis and wait for startTime
         * If system clock is > than startTime but < than endTime --> full analysis until system clock, then recurring analysis
         */
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

    /**
     * Analyzes the [CoverageAnalysis] partially until the current time, after which it starts a recurring analysis.
     * @param id The unique identifier of the [CoverageAnalysis].
     * @return The [Job] of the analysis.
     */
    private suspend fun analyzePartially(id: UUID): Job {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")
        return analysisScope.launch {
            val firstJob = analyze(id, false, analysis.startTime, Clock.System.now())
            firstJob.join()
            val secondJob = analyze(id, true, Clock.System.now(), analysis.endTime)
            secondJob.join()
        }
    }

    /**
     * Analyzes the [CoverageAnalysis] from [startTime] to [endTime] with a delay of [timeBetweenCalculations] between each calculation.
     * @param id The unique identifier of the [CoverageAnalysis].
     * @param hasWaitTime Whether to wait between each calculation.
     * @param startTime The start time of the analysis.
     * @param endTime The end time of the analysis.
     * @return The [Job] of the analysis.
     */
    private suspend fun analyze(id: UUID, hasWaitTime: Boolean, startTime: Instant, endTime: Instant): Job {
        val analysis = coverageAnalysisRepository.getBy(id) ?: throw Exception("Analysis not found")
        var currStartTime = startTime
        val job = analysisScope.launch {
            while (isActive && currStartTime < endTime) {
                val currEndTime = currStartTime.plus(analysis.timeBetweenCalculations)
                try {
                    analysis.calculateCoverage(currStartTime, currEndTime)
                    eventBus.publish(CoverageCalculatedEvent(id))
                } catch (e: Exception) {
                    eventBus.publish(CoverageCalculationFailedEvent(id, e))
                }
                currStartTime = currEndTime
                if (hasWaitTime)
                    delay(analysis.timeBetweenCalculations) // delay is in milliseconds
            }
        }
        return job
    }

    override suspend fun stopAnalysis(id: UUID) {
        val job = jobs[id] ?: throw IllegalArgumentException("No job found for analysis with id $id")
        job.cancel()
    }

    override suspend fun stopAllAnalyses() {
        analysisScope.cancel()
    }

    /**
     * Handles the [CoverageAnalysisRequestedEvent] by registering and starting the [CoverageAnalysis].
     * @param event The [Event] to handle.
     */
    private suspend fun handleCoverageAnalysisRequested(event: Event) {
        val coverageAnalysisRequestedEvent = event as CoverageAnalysisRequestedEvent
        println("Coverage analysis requested " + coverageAnalysisRequestedEvent.coverageAnalysis)
        registerAnalysis(
            coverageAnalysisRequestedEvent.coverageAnalysis
        )
        startAnalysis(coverageAnalysisRequestedEvent.coverageAnalysis.id)
    }

    private suspend fun handleCoverageAnalysisStopRequested(event: Event) {
        val coverageAnalysisStopRequestedEvent = event as CoverageAnalysisStopRequestedEvent
        try {
            stopAnalysis(coverageAnalysisStopRequestedEvent.coverageAnalysisId)
        } catch (e: Exception) {
            eventBus.publish(
                CoverageAnalysisStopFailedEvent(
                    coverageAnalysisStopRequestedEvent.coverageAnalysisId,
                    event.id,
                    e
                )
            )
        }

    }


}