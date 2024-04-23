package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CoverageVisitor : ExpectationVisitor {
    override fun visit(
        expectation: Expectation, data: List<Measurement<Data>>, startTime: Instant,
        endTime: Instant
    ): Coverage {
        val numExpectedExpectations = (endTime.minus(startTime).inWholeSeconds / expectation.timeframeSeconds)

        var windowStart = startTime
        var windowEnd = windowStart.plus(expectation.timeframeSeconds.toDuration(DurationUnit.SECONDS))
        var currCount = 0
        var totalCountMeasurements = 0
        var fulfilledExpectations = 0

        for (measurement in data) {
            // if measurements are outside of calculation window, break
            if (measurement.sensorStartTime >= endTime.toEpochMilliseconds()) {
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
                fulfilledExpectations =
                    checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations, expectation)
                // move window to next measurement
                val window = moveWindow(measurement, windowEnd, windowStart, expectation)
                windowEnd = window.first
                windowStart = window.second
                // reset count for new window
                currCount = 1
            }
        }
        fulfilledExpectations = checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations, expectation)


        val timeCoverage = fulfilledExpectations.toDouble() / numExpectedExpectations

        val absoluteCoverage =
            (totalCountMeasurements.toDouble() / (numExpectedExpectations * expectation.numDataPoints)).coerceAtMost(1.0)

        val coverage = Coverage(absoluteCoverage, timeCoverage, startTime, endTime)

        println("Coverage: ${coverage}")

        return coverage

    }

    override fun visit(
        expectation: AggregateExpectation, data: List<Measurement<Data>>, startTime: Instant,
        endTime: Instant
    ): Coverage {
        var coverage = 0.0
        for (e in expectation.expectations) {
            coverage += e.accept(this, data, startTime, endTime).timeCoverage
        }
        return Coverage(0.0, coverage / expectation.expectations.size, startTime, endTime)
    }

    private fun moveWindow(
        measurement: Measurement<Data>,
        windowEnd: Instant,
        windowStart: Instant, expectation: Expectation
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
        fulfilledExpectations: Int, expectation: Expectation
    ): Int {
        var fulfilledExpectations1 = fulfilledExpectations
        if (currCount >= expectation.numDataPoints) {
            println("Expectation met in window: $windowStart - $windowEnd")
            fulfilledExpectations1++
        }
        return fulfilledExpectations1
    }

}