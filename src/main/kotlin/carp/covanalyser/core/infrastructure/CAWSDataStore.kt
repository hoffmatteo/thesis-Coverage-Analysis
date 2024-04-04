package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataStore
import carp.ws.domain.DataPoint
import createDataPoints
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CAWSDataStore(override var deploymentId: UUID) : DataStore {
    override suspend fun obtainData(startTime: Instant, endTime: Instant): List<Measurement<Data>> {
        val dataPoints: List<DataPoint> = createDataPoints(
            100,
            listOf(10, 20, 30),
            listOf(60.toDuration(DurationUnit.MINUTES), 30.toDuration(DurationUnit.MINUTES)),
            Instant.parse("2020-06-30T14:44:01.251Z")
        )

        return convertDataPointsToMeasurements(dataPoints)
    }


    private fun convertDataPointsToMeasurements(dataPoints: List<DataPoint>): List<Measurement<Data>> {
        val measurements = mutableListOf<Measurement<Data>>()
        for (dataPoint in dataPoints) {
            val measurement = Measurement(
                data = dataPoint.carpBody as Data,
                sensorStartTime = dataPoint.carpBody?.get("timestamp") as Long,
                sensorEndTime = dataPoint.carpBody!!["timestamp"] as Long,
                dataType = DataType(
                    name = dataPoint.carpHeader?.dataFormat?.get("name") as String,
                    namespace = dataPoint.carpHeader!!.dataFormat?.get("namespace") as String
                )
            )
            measurements.add(measurement)
        }
        return measurements
    }
}