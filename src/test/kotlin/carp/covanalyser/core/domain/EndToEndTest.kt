package carp.covanalyser.core.domain

import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.infrastructure.DefaultEventBus
import carp.covanalyser.core.infrastructure.aggregation.AggregationFactory
import carp.covanalyser.core.infrastructure.aggregation.AverageCoverageAggregator
import carp.covanalyser.core.infrastructure.aggregation.ParticipantGroupAggregation
import carp.covanalyser.core.infrastructure.aggregation.StudyAggregation
import carp.covanalyser.core.infrastructure.expectations.LocationExpectation
import carp.covanalyser.core.infrastructure.expectations.StepCountExpectation
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertTimeoutPreemptively
import kotlin.test.assertEquals
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
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        val locationExpectation = LocationExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))
        val stepCountExpectation = StepCountExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))

        val coverageAggregator = AverageCoverageAggregator()


        val deviceAggregations =
            AggregationFactory().createDeviceAggregations(
                listOf(locationExpectation, stepCountExpectation),
                coverageAggregator
            )

        val participantGroupAggregation = ParticipantGroupAggregation(coverageAggregator)
        participantGroupAggregation.expectations.addAll(deviceAggregations)

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
        val slot = slot<Coverage>()
        coVerify { exportTarget.exportCoverage(capture(slot)) }

        assertEquals(expectedCoverage, slot.captured)


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