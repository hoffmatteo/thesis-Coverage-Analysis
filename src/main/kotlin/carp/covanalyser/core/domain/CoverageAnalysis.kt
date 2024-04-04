package carp.covanalyser.core.domain

import kotlinx.datetime.Instant

class CoverageAnalysis(
    var expectation: Expectation,
    var timeFrameSeconds: Int,
    var dataStore: DataStore,
    var exportTarget: ExportTarget,
    var startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    //TODO important question: Where should I put recurring calls of coverage calculation?
    // this method calculates single coverage --> from one "period"
    // somewhere I need to call this method repeatedly to calculate coverage for the
    // entire startTime and endTime --> CoverageAnalysisService?
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): Boolean {
        var data = dataStore.obtainData(calcStartTime, calcEndTime)
        //TODO important: < calcEndTime, otherwise double count
        //calc coverage with data and expectation
        val coverage = Coverage(0.0, 0.0, mutableListOf<Int>())

        exportTarget.exportCoverage(coverage)

        return true
    }


}