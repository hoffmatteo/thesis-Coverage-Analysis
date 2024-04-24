package carp.covanalyser.core.domain

import carp.covanalyser.core.application.CoverageCalculator
import carp.covanalyser.core.infrastructure.AltitudeExpectation
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.StepCount
import dk.cachet.carp.data.application.Measurement
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.Instant
import kotlinx.datetime.toInstant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CoverageAnalysisTest {

    private lateinit var expectation: Expectation
    private lateinit var coverageVisitor: CoverageVisitor
    private lateinit var coverageCalculator: CoverageCalculator
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()

    @BeforeEach
    fun setup() {
        expectation = AltitudeExpectation(10, "test", 1800)
        coverageVisitor = CoverageVisitor()
        coverageCalculator = CoverageCalculator(coverageVisitor)
    }

    @Test
    fun `calculateCoverage returns correct coverage when all expectations are met`() = runBlocking {
        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        val coverage = coverageCalculator.calculate(expectation, data, startTime, endTime)

        assertEquals(1.0, coverage.absCoverage)
        assertEquals(1.0, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct coverage when no expectations are met`() = runBlocking {
        val data = createDataPoints(8, 10, 2.toDuration(DurationUnit.MINUTES), startTime)

        val coverage = coverageCalculator.calculate(expectation, data, startTime, endTime)

        assertEquals(0.4, coverage.absCoverage)
        assertEquals(0.0, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct coverage when some expectations are met`() = runBlocking {
        val data = createDataPoints(15, 10, 1.toDuration(DurationUnit.MINUTES), startTime)

        val coverage = coverageCalculator.calculate(expectation, data, startTime, endTime)

        assertEquals(0.75, coverage.absCoverage)
        assertEquals(0.5, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct coverage when some expectations are met`() = runBlocking {
        val data = createDataPoints(15, 10, 1.toDuration(DurationUnit.MINUTES), startTime)

        val coverage = coverageCalculator.calculate(expectation, data, startTime, endTime)

        assertEquals(0.75, coverage.absCoverage)
        assertEquals(0.5, coverage.timeCoverage)
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