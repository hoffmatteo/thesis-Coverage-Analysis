package carp.covanalyser.core.domain

import kotlinx.datetime.Instant

//TODO look at databricks scheduling
class CoverageAnalysis(
    var expectation: Expectation,
    var timeFrameSeconds: Int,
    var dataStore: DataStore,
    var exportTarget: ExportTarget,
    var startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    // calculation of single coverage
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): Boolean {
        var data = dataStore.obtainData(calcStartTime, calcEndTime)
        //TODO important: < calcEndTime, otherwise double count
        //calc coverage with data and expectation
        val numExpectedExpectations = calcEndTime.minus(calcStartTime).inWholeSeconds / timeFrameSeconds

        /*
        * Two different approaches: (1) Calculate based on measurements (for measurement ...)
        *                           (2) Calculate based on timeframes (create timeframes and check measurements) --> still check every measurement
        * Can I even calculate solely based on measurements?
        * */

        // time frames are multiple of timeframeseconds?
        val coverage = Coverage(0.0, 0.0, mutableListOf<Int>())

        exportTarget.exportCoverage(coverage)

        return true
    }


}