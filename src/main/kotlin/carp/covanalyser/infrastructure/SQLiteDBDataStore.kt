package carp.covanalyser.infrastructure

import carp.covanalyser.domain.DataStore
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.infrastructure.serialization.createDefaultJSON
import dk.cachet.carp.common.infrastructure.test.STUBS_SERIAL_MODULE
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import dk.cachet.carp.data.application.MeasurementSerializer
import kotlinx.datetime.Instant
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet

/**
 * A [DataStore] implementation that uses a SQLite database to obtain  data.
 */
class SQLiteDBDataStore(dbName: String, private val tableName: String) : DataStore {
    private var connection: Connection? = null

    init {
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName")
    }


    override suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId
    ): List<Measurement<Data>> {
        val json = createDefaultJSON(STUBS_SERIAL_MODULE)

        val sql = "SELECT measurement FROM $tableName WHERE deployment_id=? AND device_rolename=? AND data_type=?"
        val preparedStatement = connection!!.prepareStatement(sql)
        preparedStatement.setString(1, dataStreamId.studyDeploymentId.toString())
        preparedStatement.setString(2, dataStreamId.deviceRoleName)
        preparedStatement.setString(3, dataStreamId.dataType.toString())
        val resultSet: ResultSet = preparedStatement.executeQuery()
        var measurements = mutableListOf<Measurement<Data>>()

        while (resultSet.next()) {
            val measurementJson = resultSet.getString("measurement")
            val measurement = json.decodeFromString(MeasurementSerializer, measurementJson)
            measurements.add(measurement)
        }

        preparedStatement.close()


        val modifiedDataPoints = measurements.map { measurement ->
            val modifiedStartTime =
                if (measurement.sensorStartTime > 9999999999999) measurement.sensorStartTime / 1000 else measurement.sensorStartTime
            val modifiedEndTime =
                if (measurement.sensorEndTime != null) {
                    if (measurement.sensorEndTime!! > 9999999999999) measurement.sensorEndTime!! / 1000 else measurement.sensorEndTime
                } else {
                    null
                }

            Measurement(
                sensorStartTime = modifiedStartTime,
                sensorEndTime = modifiedEndTime,
                dataType = measurement.dataType,
                data = measurement.data
            )
        }
        return modifiedDataPoints.filter { it.sensorStartTime in startTime.toEpochMilliseconds()..endTime.toEpochMilliseconds() }

    }
}