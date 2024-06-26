package carp.covanalyser.domain

import carp.covanalyser.infrastructure.expectations.LocationExpectation
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.StepCount
import dk.cachet.carp.data.application.Measurement
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class DataTypeCoverageTest {


    private lateinit var dataTypeExpectation: DataTypeExpectation
    private val dataStore: DataStore = mockk()
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()

    @BeforeEach
    fun setup() {
        dataTypeExpectation = LocationExpectation(10, "test", 30.toDuration(DurationUnit.MINUTES))

    }

    @Test
    fun `calculateCoverage returns correct coverage when all expectations are met`() = runBlocking {
        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            dataTypeExpectation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertTrue(coverage.absCoverage >= 1.0)
        assertTrue(coverage.expectationCoverage >= 1.0)
    }

    @Test
    fun `calculateCoverage returns correct coverage when no expectations are met`() = runBlocking {
        val data = createDataPoints(8, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            dataTypeExpectation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertEquals(0.4, coverage.absCoverage)
        assertEquals(0.0, coverage.expectationCoverage)
    }


    @Test
    fun `calculateCoverage returns correct coverage when some expectations are met`() = runBlocking {
        val data = createDataPoints(15, 10, 1.toDuration(DurationUnit.MINUTES), startTime)

        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            dataTypeExpectation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertEquals(0.75, coverage.absCoverage)
        assertEquals(0.5, coverage.expectationCoverage)
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