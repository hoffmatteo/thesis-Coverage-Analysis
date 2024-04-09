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
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): Boolean {
        var data = dataStore.obtainData(calcStartTime, calcEndTime)
        val numExpectedExpectations = (calcEndTime.minus(calcStartTime).inWholeSeconds / timeFrameSeconds)

        //TODO important: < calcEndTime, otherwise double count
        //calc coverage with data and expectation
        /*
        val timeBoxes = mutableListOf<TimeBox>()
        val currTime = calcStartTime
        while (currTime <= calcEndTime) {
            timeBoxes.add(
                TimeBox(
                    currTime,
                    currTime.plus(timeFrameSeconds.toDuration(DurationUnit.SECONDS)),
                    mutableListOf()
                )
            )
            currTime.plus(timeFrameSeconds.toDuration(DurationUnit.SECONDS))
        }
        // TODO can assume data is sorted by time?
        // TODO this is pretty inefficient, for sure a better way exists
        for (measurement in data) {
            for (timeBox in timeBoxes) {
                if (measurement.sensorStartTime >= timeBox.startTime.toEpochMilliseconds() && measurement.sensorEndTime!! <= timeBox.endTime.toEpochMilliseconds()) {
                    timeBox.measurements.add(measurement)
                }
            }
        }

         */
        var windowStart = calcStartTime
        var windowEnd = windowStart.plus(timeFrameSeconds.toDuration(DurationUnit.SECONDS))
        var count = 0
        var fulfilledExpectations = 0
        for (measurement in data) {
            if (measurement.sensorStartTime >= windowStart.toEpochMilliseconds() && measurement.sensorStartTime < windowEnd.toEpochMilliseconds()) {
                // count for current window
                count++
            } else {
                // check if expectation was met in last window
                if (count >= expectation.numDataPoints) {
                    fulfilledExpectations++
                }
                while (measurement.sensorStartTime > windowEnd.toEpochMilliseconds()) {
                    windowStart = windowStart.plus(timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                    windowEnd = windowEnd.plus(timeFrameSeconds.toDuration(DurationUnit.SECONDS))
                }
                count = 1
            }
        }
        var timeCoverage = (fulfilledExpectations / numExpectedExpectations).toDouble()
        // time frames are multiple of timeframeseconds?
        val coverage = Coverage(0.0, timeCoverage, mutableListOf<Int>())

        println("Coverage: ${coverage.timeCoverage}")

        exportTarget.exportCoverage(coverage)

        return true
    }

    data class TimeBox(val startTime: Instant, val endTime: Instant, val measurements: MutableList<Measurement<Data>>)


}