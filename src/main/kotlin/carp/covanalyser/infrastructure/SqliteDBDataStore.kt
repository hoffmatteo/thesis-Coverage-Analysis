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
import java.sql.Statement

class SqliteDBDataStore : DataStore {
    private var connection: Connection? = null
    private var dbName: String = "carp-data.db"
    private var tableName: String = "measurements"

    init {
        Class.forName("org.sqlite.JDBC")
        connection = DriverManager.getConnection("jdbc:sqlite:$dbName")
    }


    override suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId
    ): List<Measurement<Data>> {
        val json = createDefaultJSON(STUBS_SERIAL_MODULE)
        val statement: Statement = connection!!.createStatement()
        val resultSet: ResultSet =
            statement.executeQuery(
                "SELECT measurement FROM $tableName WHERE deployment_id='${dataStreamId.studyDeploymentId}'" +
                        "AND device_rolename='${dataStreamId.deviceRoleName}' AND data_type='${dataStreamId.dataType}'"
            )
        val measurements = mutableListOf<Measurement<Data>>()

        while (resultSet.next()) {
            val measurementJson = resultSet.getString("measurement")
            val measurement = json.decodeFromString(MeasurementSerializer, measurementJson)
            measurements.add(measurement)
        }

        statement.close()
        
        measurements.filter { it.sensorStartTime in startTime.toEpochMilliseconds()..endTime.toEpochMilliseconds() }

        val modifiedDataPoints = measurements.map { measurement ->
            val modifiedStartTime = measurement.sensorStartTime / 1000
            Measurement(
                sensorStartTime = modifiedStartTime,
                sensorEndTime = measurement.sensorEndTime,
                dataType = measurement.dataType,
                data = measurement.data
            )
        }
        print(modifiedDataPoints)
        return modifiedDataPoints
    }
}