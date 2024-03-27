import carp.ws.domain.DataPoint
import carp.ws.domain.DataPointHeaderDto
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.devices.Smartphone
import dk.cachet.carp.common.application.sampling.Granularity
import dk.cachet.carp.common.infrastructure.serialization.JSON
import dk.cachet.carp.protocols.domain.StudyProtocol
import dk.cachet.carp.protocols.domain.start
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString

fun main(args: Array<String>) {
    // when I call endpoint, I receive list of DataPoint objects like this --> use it to analyze
    val dataPoint = DataPoint(
        id = 1,
        deploymentId = "c9d341ae-2209-4a70-b9a1-09446bb05dca",
        carpHeader = DataPointHeaderDto(
            studyId = "8",
            userId = "user@dtu.dk",
            dataFormat = hashMapOf(
                "name" to "location",
                "namespace" to "carp"
            ),
            triggerId = "task1",
            deviceRoleName = "Patient's phone",
            uploadTime = Instant.parse("2020-06-30T14:44:01.182Z"),
            startTime = Instant.parse("2018-11-08T15:30:40.721748Z"),
            endTime = Instant.parse("2020-06-30T14:44:01.182Z")
        ),
        carpBody = hashMapOf(
            "altitude" to 43.3,
            "device_info" to hashMapOf<String, Any>(), // Empty HashMap
            "classname" to "LocationDatum",
            "latitude" to 23454.345,
            "accuracy" to 12.4,
            "speed_accuracy" to 12.3,
            "id" to "3fdd1760-bd30-11e8-e209-ef7ee8358d2f",
            "speed" to 2.3,
            "longitude" to 23.4,
            "timestamp" to Instant.parse("2018-11-08T15:30:40.721748Z")
        ),
        createdBy = "1",
        updatedBy = "1",
        createdAt = Instant.parse("2020-06-30T14:44:01.251Z"),
        updatedAt = Instant.parse("2020-06-30T14:44:01.251Z")
    )
    //var data: Data = dataPoint.carpBody as Data


}


public fun createStudyProtocol() {
    // Create a new study protocol.
    val ownerId = UUID.randomUUID()
    val protocol = StudyProtocol(ownerId, "Track patient movement")

    // Define which devices are used for data collection.
    val phone = Smartphone.create("Patient's phone")
    {
        // Configure device-specific options, e.g., frequency to collect data at.
        defaultSamplingConfiguration {
            geolocation { batteryNormal { granularity = Granularity.Balanced } }
        }
    }
    protocol.addPrimaryDevice(phone)

    // Define what needs to be measured, on which device, when.
    val sensors = Smartphone.Sensors
    val trackMovement = Smartphone.Tasks.BACKGROUND.create("Track movement") {
        measures = listOf(sensors.GEOLOCATION.measure(), sensors.STEP_COUNT.measure())
        description = "Track activity level and number of places visited per day."
    }
    protocol.addTaskControl(phone.atStartOfStudy().start(trackMovement, phone))

    // JSON output of the study protocol, compatible with the rest of the CARP infrastructure.
    val json: String = JSON.encodeToString(protocol.getSnapshot())
}
