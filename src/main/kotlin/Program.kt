import carp.ws.domain.DataPoint
import carp.ws.domain.DataPointHeaderDto
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

fun main(args: Array<String>) {
    // when I call endpoint, I receive list of DataPoint objects like this --> use it to analyze

    //var data: Data = dataPoint.carpBody as Data

    var dataPoints: List<DataPoint> = createDataPoints(
        100,
        listOf(10, 20, 30),
        listOf(60.toDuration(DurationUnit.MINUTES), 30.toDuration(DurationUnit.MINUTES)),
        Instant.parse("2020-06-30T14:44:01.251Z")
    )

    var expectation = Expectation(
        numDataPoints = 2,
        isValid = { input: DataPoint -> input.carpBody?.get("altitude") as Double > 0 },
        dataSource = "location",
        timeframeSeconds = 3600
    )

    var coverage = Coverage(expectation, 0)

    println(coverage.calculateCoverage(dataPoints, Instant.parse("2020-06-30T14:44:01.251Z")))


}

fun createDataPoints(
    numberOfObjects: Int,
    latitudeList: List<Int>,
    durationList: List<Duration>,
    startTime: Instant
): List<DataPoint> {
    val dataPoints = mutableListOf<DataPoint>()
    var createdAt = startTime

    repeat(numberOfObjects) {
        val latitude = latitudeList.random()
        val duration = durationList.random()
        val dataPoint = DataPoint(
            carpHeader = DataPointHeaderDto(),
            carpBody = hashMapOf(
                "altitude" to 20.3,
                "device_info" to hashMapOf<String, Any>(), // Empty HashMap
                "classname" to "LocationDatum",
                "latitude" to latitude.toDouble(),
                "accuracy" to 12.4,
                "speed_accuracy" to 12.3,
                "id" to "3fdd1760-bd30-11e8-e209-ef7ee8358d2f",
                "speed" to 2.3,
                "longitude" to 23.4,
                "timestamp" to Clock.System.now()
            ),
            createdBy = (it + 1).toString(),
            createdAt = createdAt,
        )
        dataPoints.add(dataPoint)
        createdAt = createdAt.plus(duration)
    }
    return dataPoints
}


