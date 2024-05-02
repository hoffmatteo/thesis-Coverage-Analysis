package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataStore
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.infrastructure.serialization.createDefaultJSON
import dk.cachet.carp.common.infrastructure.test.STUBS_SERIAL_MODULE
import dk.cachet.carp.data.application.DataStreamBatch
import dk.cachet.carp.data.application.DataStreamBatchSerializer
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import java.io.File

class CAWSDataStore : DataStore {
    // batch is collection of non-overlapping data stream sequences --> always same deploymentID?
    private lateinit var streamBatch: DataStreamBatch

    init {
        val json = createDefaultJSON(STUBS_SERIAL_MODULE)
        val file = File("src/main/kotlin/carp/covanalyser/core/data-1.json").readText()
        streamBatch = json.decodeFromString(DataStreamBatchSerializer, file)
    }

    override suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId
    ): List<Measurement<Data>> {
        /*
        val dataPoints: List<Measurement<Data>> = createDataPoints(
            1000,
            listOf(10, 20, 30),
            listOf(10.toDuration(DurationUnit.SECONDS), 10.toDuration(DurationUnit.SECONDS)),
            Instant.parse("2020-06-29T14:44:01.251Z")
        )
         */
        val dataStreamPoints = streamBatch.getDataStreamPoints(dataStreamId).toList()
        dataStreamPoints.filter { it.measurement.sensorStartTime in startTime.toEpochMilliseconds()..endTime.toEpochMilliseconds() }
        val dataPoints = dataStreamPoints.map { it.measurement }

        val modifiedDataPoints = dataPoints.map { measurement ->
            val modifiedStartTime = measurement.sensorStartTime / 1000
            Measurement(
                sensorStartTime = modifiedStartTime,
                sensorEndTime = measurement.sensorEndTime,
                dataType = measurement.dataType,
                data = measurement.data
            )
        }
        return modifiedDataPoints
    }
}