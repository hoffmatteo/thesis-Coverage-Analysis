package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Represents an [Expectation] for a specific [DataType] on a specific device.
 *
 * @param numDataPoints The number of measurements expected in the given [duration].
 * @param dataType The [DataType] of the measurements.
 * @param deviceName The name of the device the measurements are expected from.
 * @param duration The duration of the expectation.
 */
abstract class DataTypeExpectation(
    var numDataPoints: Int,
    var dataType: DataType,
    var deviceName: String,
    var duration: Duration
) : Expectation {
    /**
     * Returns whether a [Measurement] is valid for the expectation.
     *
     * @param input The [Measurement] to check.
     */
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
            // create data stream id to obtain data from
            val dataStreamId = DataStreamId(deploymentID, deviceName, dataType)

            //obtain data from data store using dataStreamId
            val data = dataStore.obtainData(startTime, endTime, dataStreamId)

            // validate duration is not longer than duration of data
            validateDuration(startTime, endTime)

            // calculate number of expected expectations
            val numExpectedExpectations = calculateNumExpectedExpectations(startTime, endTime)

            val coverageResult = calculateCoverageInWindows(data, startTime, endTime, numExpectedExpectations)

            val metadata = createCoverageMetadata(coverageResult, deploymentID)

            println("Coverage Data Stream: $metadata")
            coverages.add(metadata)
        }

        return coverages
    }

    /**
     * Validates that the duration of the expectation is not longer than the duration of the data.
     */
    private fun validateDuration(startTime: Instant, endTime: Instant) {
        if (duration > endTime.minus(startTime)) {
            throw IllegalArgumentException("Duration of expectation is longer than duration of data")
        }
    }

    /**
     * Calculates the number of expected expectations in the given time frame.
     */
    private fun calculateNumExpectedExpectations(startTime: Instant, endTime: Instant): Int {
        return (endTime.minus(startTime) / duration).toInt()
    }

    /**
     * Calculates the coverage in expectation windows of length [duration] for the given data, starting from [startTime] and ending at [endTime].
     */
    private fun calculateCoverageInWindows(
        data: List<Measurement<Data>>,
        startTime: Instant,
        endTime: Instant,
        numExpectedExpectations: Int
    ): Coverage {
        var windowStart = startTime
        var windowEnd = windowStart.plus(duration)
        var currCount = 0
        var totalCountMeasurements = 0
        var fulfilledExpectations = 0

        for (measurement in data) {
            // measurement is outside the coverage time frame
            if (measurement.sensorStartTime >= endTime.toEpochMilliseconds()) break

            // measurement is part of coverage time frame
            totalCountMeasurements++

            if (!isValid(measurement)) continue

            // if inside of current expectation window, count it
            if (isInCurrentWindow(measurement, windowStart, windowEnd)) {
                currCount++
            } else {
                // current expectation window has ended, check if the expectation was fulfilled
                fulfilledExpectations = checkExpectation(currCount, fulfilledExpectations)

                // move window to next expectation window based on measurement
                val (newWindowEnd, newWindowStart) = moveWindow(measurement, windowEnd, windowStart)
                windowEnd = newWindowEnd
                windowStart = newWindowStart
                currCount = 1
            }
        }
        // check if last expectation was fulfilled
        fulfilledExpectations = checkExpectation(currCount, fulfilledExpectations)

        val timeCoverage = calculateTimeCoverage(fulfilledExpectations, numExpectedExpectations)
        val absoluteCoverage = calculateAbsoluteCoverage(totalCountMeasurements, numExpectedExpectations)

        return Coverage(absoluteCoverage, timeCoverage, startTime, endTime)
    }

    /**
     * Checks if a [Measurement] is in the current expectation window.
     */
    private fun isInCurrentWindow(
        measurement: Measurement<Data>,
        windowStart: Instant,
        windowEnd: Instant
    ): Boolean {
        val sensorTime = measurement.sensorStartTime
        return sensorTime >= windowStart.toEpochMilliseconds() && sensorTime < windowEnd.toEpochMilliseconds()
    }

    /**
     * Moves the window to the next expectation window based on the given [measurement].
     */
    private fun moveWindow(
        measurement: Measurement<Data>,
        windowEnd: Instant,
        windowStart: Instant
    ): Pair<Instant, Instant> {
        var newWindowEnd = windowEnd
        var newWindowStart = windowStart
        while (measurement.sensorStartTime >= newWindowEnd.toEpochMilliseconds()) {
            newWindowStart = newWindowStart.plus(duration)
            newWindowEnd = newWindowEnd.plus(duration)
        }
        return Pair(newWindowEnd, newWindowStart)
    }

    /**
     * Checks if the current expectation was fulfilled and returns the updated count of fulfilled expectations.
     */
    private fun checkExpectation(
        currCount: Int,
        fulfilledExpectations: Int
    ): Int {
        return if (currCount >= numDataPoints) {
            fulfilledExpectations + 1
        } else {
            fulfilledExpectations
        }
    }

    /**
     * Calculates the time coverage based on the number of fulfilled expectations and the number of expected expectations.
     */
    private fun calculateTimeCoverage(
        fulfilledExpectations: Int,
        numExpectedExpectations: Int
    ): Double {
        return fulfilledExpectations.toDouble() / numExpectedExpectations
    }

    /**
     * Calculates the absolute coverage based on the total number of measurements and the total number of expected measurements.
     */
    private fun calculateAbsoluteCoverage(
        totalCountMeasurements: Int,
        numExpectedExpectations: Int
    ): Double {
        return totalCountMeasurements.toDouble() / (numExpectedExpectations * numDataPoints)
    }

    /**
     * Creates a [CoverageWithMetadata] object with the given [coverage] and [deploymentID].
     */
    private fun createCoverageMetadata(
        coverage: Coverage,
        deploymentID: UUID
    ): CoverageWithMetadata {
        return CoverageWithMetadata(coverage, listOf(deploymentID), getDescription())
    }
}