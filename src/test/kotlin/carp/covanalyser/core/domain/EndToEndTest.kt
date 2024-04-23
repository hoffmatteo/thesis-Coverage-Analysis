package carp.covanalyser.core.domain

import carp.covanalyser.core.application.CoverageCalculator
import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.infrastructure.AltitudeExpectation
import carp.covanalyser.core.infrastructure.DefaultEventBus
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
    private lateinit var coverageAnalysis: CoverageAnalysis
    private val dataStore: DataStore = mockk()
    private val exportTarget: ExportTarget = mockk(relaxed = true)
    private lateinit var expectation: Expectation
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()


    @BeforeEach
    fun setup() {
        expectation = AltitudeExpectation(10, "test", 1800)
        coverageAnalysis = CoverageAnalysis(expectation, 3600, dataStore, exportTarget, startTime, endTime)
    }


    @Test
    fun `end to end test returns correct coverage`() = runBlocking {
        val eventBus = DefaultEventBus()
        val coverageVisitor = CoverageVisitor()
        val coverageCalculator = CoverageCalculator(coverageVisitor)
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus, coverageCalculator)

        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)


        val coverageAnalysis = CoverageAnalysis(
            expectation,
            3600,
            dataStore,
            exportTarget,
            startTime,
            endTime
        )

        coEvery { dataStore.obtainData(any(), any()) } returns data

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