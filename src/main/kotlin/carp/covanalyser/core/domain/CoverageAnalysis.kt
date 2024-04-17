package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
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
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): Coverage {
        val data = dataStore.obtainData(calcStartTime, calcEndTime)
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
            }
            // count all fitting measurements for absolute coverage
            totalCountMeasurements++

            // if measurement is inside of current window
            if (measurement.sensorStartTime >= windowStart.toEpochMilliseconds() && measurement.sensorStartTime < windowEnd.toEpochMilliseconds()) {
                // count for current window
                currCount++
            } else {
                // check if expectation was met in last window
                fulfilledExpectations = checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations)
                // move window to next measurement
                val window = moveWindow(measurement, windowEnd, windowStart)
                windowEnd = window.first
                windowStart = window.second
                // reset count for new window
                currCount = 1
            }
        }
        fulfilledExpectations = checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations)


        val timeCoverage = fulfilledExpectations.toDouble() / numExpectedExpectations

        val absoluteCoverage =
            (totalCountMeasurements.toDouble() / (numExpectedExpectations * expectation.numDataPoints)).coerceAtMost(1.0)

        val coverage = Coverage(absoluteCoverage, timeCoverage, calcStartTime, calcEndTime)

        println("Coverage: ${coverage}")

        exportTarget.exportCoverage(coverage)

        return coverage
    }

    private fun moveWindow(
        measurement: Measurement<Data>,
        windowEnd: Instant,
        windowStart: Instant
    ): Pair<Instant, Instant> {
        var windowEnd1 = windowEnd
        var windowStart1 = windowStart
        while (measurement.sensorStartTime >= windowEnd1.toEpochMilliseconds()) {
            windowStart1 = windowStart1.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
            windowEnd1 = windowEnd1.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
        }
        return Pair(windowEnd1, windowStart1)
    }

    private fun checkExpectation(
        currCount: Int,
        windowStart: Instant,
        windowEnd: Instant,
        fulfilledExpectations: Int
    ): Int {
        var fulfilledExpectations1 = fulfilledExpectations
        if (currCount >= expectation.numDataPoints) {
            println("Expectation met in window: $windowStart - $windowEnd")
            fulfilledExpectations1++
        }
        return fulfilledExpectations1
    }

    override fun toString(): String {
        return "CoverageAnalysis(expectation=$expectation, timeFrameSeconds=$timeFrameSeconds, dataStore=$dataStore, exportTarget=$exportTarget, startTime=$startTime, endTime=$endTime)"
    }


}