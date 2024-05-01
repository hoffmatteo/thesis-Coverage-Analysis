package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.Duration

abstract class DataStreamExpectation(
    private var numDataPoints: Int,
    private var dataType: DataType,
    var deviceName: String,
    private var duration: Duration
) : Expectation {

    abstract fun isConformant(input: Measurement<Data>): Boolean

    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage> {
        val coverages = mutableListOf<Coverage>()
        for (deploymentID in deploymentIDs) {
            var dataStreamId = DataStreamId(deploymentID, deviceName, dataType)
            val data = dataStore.obtainData(startTime, endTime, dataStreamId)

            val numExpectedExpectations = (endTime.minus(startTime).inWholeSeconds / duration.inWholeSeconds)

            var windowStart = startTime
            var windowEnd = windowStart.plus(duration)
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
                        checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations)
                    // move window to next measurement
                    val window = moveWindow(measurement, windowEnd, windowStart)
                    windowEnd = window.first
                    windowStart = window.second
                    // reset count for new window
                    currCount = 1
                }
            }
            fulfilledExpectations =
                checkExpectation(currCount, windowStart, windowEnd, fulfilledExpectations)


            val timeCoverage = fulfilledExpectations.toDouble() / numExpectedExpectations

            val absoluteCoverage =
                (totalCountMeasurements.toDouble() / (numExpectedExpectations * numDataPoints)).coerceAtMost(
                    1.0
                )

            val coverage = Coverage(absoluteCoverage, timeCoverage, startTime, endTime)

            println("Coverage Data Stream: ${coverage}")

            coverages.add(coverage)
        }
        return coverages
    }

    private fun moveWindow(
        measurement: Measurement<Data>,
        windowEnd: Instant,
        windowStart: Instant
    ): Pair<Instant, Instant> {
        var windowEnd1 = windowEnd
        var windowStart1 = windowStart
        while (measurement.sensorStartTime >= windowEnd1.toEpochMilliseconds()) {
            windowStart1 = windowStart1.plus(duration)
            windowEnd1 = windowEnd1.plus(duration)
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
        if (currCount >= numDataPoints) {
            println("Expectation met in window: $windowStart - $windowEnd")
            fulfilledExpectations1++
        }
        return fulfilledExpectations1
    }
}