package carp.covanalyser.domain

import carp.covanalyser.application.DefaultCoverageAnalysisService
import carp.covanalyser.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.infrastructure.DefaultEventBus
import carp.covanalyser.infrastructure.InMemoryCoverageAnalysisRepository
import carp.covanalyser.infrastructure.expectations.LocationExpectation
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.StepCount
import dk.cachet.carp.data.application.Measurement
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlin.time.toJavaDuration

class EndToEndTest {
    private val dataStore: DataStore = mockk()
    private val exportTarget: ExportTarget = mockk(relaxed = true)
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()


    @BeforeEach
    fun setup() {
    }


    @Test
    fun `end to end test returns correct coverage`() = runBlocking {
        val eventBus = DefaultEventBus()
        val coverageAnalysisRepository = InMemoryCoverageAnalysisRepository()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus, coverageAnalysisRepository)

        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        val locationExpectation = LocationExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))

        val coverageAnalysis = CoverageAnalysis(
            listOf(locationExpectation),
            1.toDuration(DurationUnit.HOURS),
            listOf(UUID.randomUUID()),
            exportTarget,
            dataStore,
            startTime,
            endTime
        )

        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val channel = Channel<Unit>()

        val reqEvent = CoverageAnalysisRequestedEvent(coverageAnalysis, coverageAnalysis.id)

        eventBus.publish(reqEvent)
        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { event ->
            runBlocking {
                channel.send(Unit)
                assertEquals(reqEvent.id, event.id)
            }

        }
        assertTimeoutPreemptively(3.toDuration(DurationUnit.MINUTES).toJavaDuration()) {
            runBlocking {
                channel.receive()
            }
        }

        val expectedCoverage = Coverage(1.5, 1.0, startTime, endTime)
        val slot = slot<List<CoverageWithMetadata>>()
        coVerify { exportTarget.exportCoverage(capture(slot), coverageAnalysis) }

        assertEquals(expectedCoverage, slot.captured.first().coverage)


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
}