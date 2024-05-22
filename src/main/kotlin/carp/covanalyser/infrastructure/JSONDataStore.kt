package carp.covanalyser.infrastructure

import carp.covanalyser.domain.DataStore
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.infrastructure.serialization.createDefaultJSON
import dk.cachet.carp.common.infrastructure.test.STUBS_SERIAL_MODULE
import dk.cachet.carp.data.application.DataStreamBatch
import dk.cachet.carp.data.application.DataStreamBatchSerializer
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import java.io.File

class JSONDataStore(val filePath: String) : DataStore {
    private var streamBatch: DataStreamBatch

    init {
        val json = createDefaultJSON(STUBS_SERIAL_MODULE)
        val file = File(filePath).readText()
        streamBatch = json.decodeFromString(DataStreamBatchSerializer, file)
    }

    override suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId
    ): List<Measurement<Data>> {
        val dataStreamPoints = streamBatch.getDataStreamPoints(dataStreamId).toList()
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
        return modifiedDataPoints.filter { it.sensorStartTime in startTime.toEpochMilliseconds()..endTime.toEpochMilliseconds() }
    }
}