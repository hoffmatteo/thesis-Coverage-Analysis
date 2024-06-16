package carp.covanalyser.domain

import io.mockk.mockk
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.BeforeEach

class EndToEndTest {
    private val dataStore: DataStore = mockk()
    private val exportTarget: ExportTarget = mockk(relaxed = true)
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()


    @BeforeEach
    fun setup() {
    }

    /*
    @Test
    fun `end to end test returns correct coverage`() = runBlocking {
        val eventBus = DefaultEventBus()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        val locationExpectation = LocationExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))
        val stepCountExpectation = StepCountExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))

        val dataStreams = listOf(locationExpectation, stepCountExpectation)

        val coverageAggregator = AverageCoverageAggregator()


        val deviceAggregations =
            AggregationFactory().createDeviceAggregations(
                dataStreams,
                coverageAggregator
            )

        val participantGroupAggregation = ParticipantGroupAggregation(coverageAggregator)
        participantGroupAggregation.expectations.addAll(dataStreams)

        val studyAggregation = StudyAggregation(coverageAggregator)
        studyAggregation.expectations.add(participantGroupAggregation)


        val coverageAnalysis = CoverageAnalysis(
            studyAggregation,
            1.toDuration(DurationUnit.HOURS),
            listOf(UUID.randomUUID()),
            exportTarget,
            dataStore,
            startTime,
            endTime
        )

        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val channel = Channel<Unit>()

        val reqEvent = CoverageAnalysisRequestedEvent(coverageAnalysis)

        eventBus.publish(reqEvent)
        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { event ->
            runBlocking {
                channel.send(Unit)
                assertEquals(reqEvent.id, event.id)
            }

        }
        assertTimeoutPreemptively(1.toDuration(DurationUnit.MINUTES).toJavaDuration()) {
            runBlocking {
                channel.receive()
            }
        }

        val expectedCoverage = Coverage(1.0, 1.0, startTime, endTime)
        val slot = slot<List<Coverage>>()
        coVerify { exportTarget.exportCoverage(capture(slot)) }

        assertEquals(expectedCoverage, slot.captured.first())


    }

    private fun createDataPoints(
        numberOfObjects: Int,
        stepCount: Int,
        duration: Duration,
        startTime: Instant
    ): List<Measurement<Data>> {
        val dataPoints = mutableListOf<Measurement<Data>>()
        var createdAt = startTime

        repeat(numberOfObjects) {
            val stepCountObject = StepCount(stepCount)
            val measurement = Measurement(
                data = stepCountObject,
                sensorStartTime = createdAt.toEpochMilliseconds(),
                dataType = CarpDataTypes.STEP_COUNT.type,
                sensorEndTime = null
            )
            dataPoints.add(measurement)
            createdAt = createdAt.plus(duration)
        }
        return dataPoints
    }


     */

}