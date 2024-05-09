import carp.covanalyser.infrastructure.StartUp
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.StepCount
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.Duration

suspend fun main() {
    // val apiHandler = APIHandler()
    // apiHandler.handleRequest("/coverage-analysis")
    StartUp().startUp()
    // two expectations defined for a single data stream for a single deployment


    // second step: aggregate to deployment
    //val deploymentAggregation: DeploymentAggregation = DeploymentAggregation(UUID.randomUUID())
    //deploymentAggregation.expectations.add(deviceAggregation)

    // third step: aggregate to study
    //val studyAggregation: StudyAggregation = StudyAggregation(UUID.randomUUID())
    //studyAggregation.expectations.add(deploymentAggregation)

    // do this for every deployment inside study?


    // very easy way to calculate coverage: you have study protocol, you create expectation structure from
    // that based on what you want


}

fun createDataPoints(
    numberOfObjects: Int,
    stepCountList: List<Int>,
    durationList: List<Duration>,
    startTime: Instant
): List<Measurement<Data>> {
    val dataPoints = mutableListOf<Measurement<Data>>()
    var createdAt = startTime

    repeat(numberOfObjects) {
        val stepCount = StepCount(stepCountList.random())
        val measurement = Measurement(
            data = stepCount,
            sensorStartTime = createdAt.toEpochMilliseconds(),
            dataType = CarpDataTypes.STEP_COUNT.type,
            sensorEndTime = null
        )
        dataPoints.add(measurement)
        createdAt = createdAt.plus(durationList.random())
    }
    return dataPoints
}


