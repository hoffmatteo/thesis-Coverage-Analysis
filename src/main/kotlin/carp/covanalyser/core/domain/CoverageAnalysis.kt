package carp.covanalyser.core.domain

import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

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
        println("hello")
        var data = dataStore.obtainData(calcStartTime, calcEndTime)
        val numExpectedExpectations = (calcEndTime.minus(calcStartTime).inWholeSeconds / expectation.timeframeSeconds)

        var windowStart = calcStartTime
        var windowEnd = windowStart.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
        var currCount = 0
        var totalCountMeasurements = 0
        var fulfilledExpectations = 0


        for (measurement in data) {
            // if measurements are outside of calculation window, break
            if (measurement.sensorStartTime >= calcEndTime.toEpochMilliseconds()) {
                break
            } else {
                // count all fitting measurements for absolute coverage
                totalCountMeasurements++
            }
            if (measurement.sensorStartTime >= windowStart.toEpochMilliseconds() && measurement.sensorStartTime < windowEnd.toEpochMilliseconds()) {
                // count for current window
                currCount++
            } else {
                // check if expectation was met in last window
                if (currCount >= expectation.numDataPoints) {
                    fulfilledExpectations++
                }
                // move window to next measurement
                while (measurement.sensorStartTime > windowEnd.toEpochMilliseconds()) {
                    windowStart = windowStart.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
                    windowEnd = windowEnd.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
                }
                // reset count for new window
                currCount = 1
            }
        }

        val timeCoverage = fulfilledExpectations.toDouble() / numExpectedExpectations
        var absoluteCoverage =
            totalCountMeasurements.toDouble() / (numExpectedExpectations * expectation.numDataPoints)
        if (absoluteCoverage > 1.0) {
            absoluteCoverage = 1.0
        }
        // time frames are multiple of timeframeseconds?
        val coverage = Coverage(absoluteCoverage, timeCoverage, mutableListOf<Int>())

        println("Coverage: ${coverage}")

        exportTarget.exportCoverage(coverage)

        return true
    }

    override fun toString(): String {
        return "CoverageAnalysis(expectation=$expectation, timeFrameSeconds=$timeFrameSeconds, dataStore=$dataStore, exportTarget=$exportTarget, startTime=$startTime, endTime=$endTime)"
    }


}