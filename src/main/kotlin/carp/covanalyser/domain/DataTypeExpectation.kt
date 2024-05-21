package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.Duration

abstract class DataTypeExpectation(
    var numDataPoints: Int,
    var dataType: DataType,
    var deviceName: String,
    var duration: Duration
) : Expectation {

    abstract fun isValid(input: Measurement<Data>): Boolean

    override fun getDescription(): String {
        return "Expectation for $deviceName: $numDataPoints data points of type $dataType in $duration"
    }

    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (deploymentID in deploymentIDs) {
            val dataStreamId = DataStreamId(deploymentID, deviceName, dataType)
            val data = dataStore.obtainData(startTime, endTime, dataStreamId)
            //TODO ensure duration of expectation is <= duration of data! throw execption if not
            val numExpectedExpectations = (endTime.minus(startTime) / duration).toInt()

            var windowStart = startTime
            var windowEnd = windowStart.plus(duration)
            var currCount = 0
            var totalCountMeasurements = 0
            var fulfilledExpectations = 0

            for (measurement in data) {
                //TODO USE SYNC POINT?
                // milli seconds versus microseconds
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

            val timeCoverage = (fulfilledExpectations.toDouble() / numExpectedExpectations)

            val absoluteCoverage =
                (totalCountMeasurements.toDouble() / (numExpectedExpectations * numDataPoints))


            val coverage =
                Coverage(absoluteCoverage, timeCoverage, startTime, endTime)

            val metadata = CoverageWithMetadata(coverage, deploymentIDs, getDescription())


            println("Coverage Data Stream: ${metadata}")

            coverages.add(metadata)
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
            //println("Expectation met in window: $windowStart - $windowEnd")
            fulfilledExpectations1++
        } else {
            //println("Expectation not met in window: $windowStart - $windowEnd")
        }
        return fulfilledExpectations1
    }
}