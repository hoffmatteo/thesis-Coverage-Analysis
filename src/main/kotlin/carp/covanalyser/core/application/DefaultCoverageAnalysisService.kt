package carp.covanalyser.core.application

import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.EventBus
import carp.covanalyser.core.domain.CoverageAnalysis
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

/**
 * Manage coverage analysis
 */
class DefaultCoverageAnalysisService(var eventBus: EventBus) : CoverageAnalysisService {
    //TODO how do I model  coverage of a single participant versus of an entire study?
    private val analyses = mutableMapOf<String, CoverageAnalysis>()
    private val jobs = mutableMapOf<String, Job>()
    private val serviceScope = CoroutineScope(Dispatchers.Default)

    //TODO listen to events and call functions based on that
    override fun registerAnalysis(id: String, analysis: CoverageAnalysis) {
        analyses[id] = analysis
    }

    // TODO some analysis is recurring, some not --> e.g. only check over 1 month: survey done? not recurring
    override fun startRecurringAnalysis(id: String) {
        val analysis = analyses[id]
        analysis?.let {
            val job = serviceScope.launch {
                val currTime = it.startTime
                while (isActive && currTime <= it.endTime) {
                    it.calculateCoverage(currTime, currTime.plus(it.timeFrameSeconds.toDuration(DurationUnit.SECONDS)))
                    currTime.plus(it.timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                    delay(it.timeFrameSeconds * 1000L) // delay is in milliseconds
                }
                eventBus.publish(CoverageAnalysisCompletedEvent())
            }
            jobs[id] = job
        }
    }

    override fun startFullAnalysis(id: String) {
        val analysis = analyses[id]
        analysis?.let {
            val job = serviceScope.launch {
                var currTime = it.startTime
                var currEndTime: Instant
                while (isActive && currTime <= it.endTime) {
                    currEndTime = currTime.plus(it.timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                    it.calculateCoverage(currTime, currEndTime)
                    currTime = currEndTime
                }
                eventBus.publish(CoverageAnalysisCompletedEvent())
            }
            jobs[id] = job
        }
    }

    override fun stopAnalysis(id: String) {
        jobs[id]?.cancel()
    }

    override fun stopAllAnalyses() {
        serviceScope.cancel()
    }

}