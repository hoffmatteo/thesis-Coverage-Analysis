package carp.covanalyser.core.application

import carp.covanalyser.core.domain.CoverageAnalysis
import kotlinx.coroutines.*

/**
 * Manage coverage analysis
 */
class CoverageAnalysisService {
    val analyses = mutableMapOf<String, CoverageAnalysis>()
    private val jobs = mutableMapOf<String, Job>()
    private val serviceScope = CoroutineScope(Dispatchers.Default)


    fun registerAnalysis(id: String, analysis: CoverageAnalysis) {
        analyses[id] = analysis
    }

    // at some point there needs to be recurring calls based on timeframe in Expectation --> who should handle that?
    // probably this service? CoverageAnalysis itself just does the calculation
    fun startAnalysis(id: String) {
        val analysis = analyses[id]
        analysis?.let {
            val job = serviceScope.launch {
                while (isActive) {
                    it.calculateCoverage()
                    delay(it.timeFrameSeconds * 1000L) // delay is in milliseconds
                }
            }
            jobs[id] = job
        }
    }

    fun stopAnalysis(id: String) {
        jobs[id]?.cancel()
    }

    fun stopAllAnalyses() {
        serviceScope.cancel()
    }

}