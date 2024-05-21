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

class MultiJSONDataStore : DataStore {
    // batch is collection of non-overlapping data stream sequences --> always same deploymentID?
    private lateinit var streamBatch: DataStreamBatch

    init {
        val json = createDefaultJSON(STUBS_SERIAL_MODULE)
        // read from file_1, file_2 etc. to 10 and combine into one string

        var file = File("test_data\\combined_data_streams.json").readText()
        /*file += File("test_data\\file_2.json").readText()
        file += File("test_data\\file_3.json").readText()
        file += File("test_data\\file_4.json").readText()
        file += File("test_data\\file_5.json").readText()
        file += File("test_data\\file_6.json").readText()
        file += File("test_data\\file_7.json").readText()
        file += File("test_data\\file_8.json").readText()
        file += File("test_data\\file_9.json").readText()
        file += File("test_data\\file_10.json").readText()

         */


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