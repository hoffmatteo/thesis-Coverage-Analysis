package carp.covanalyser.core.domain

import io.mockk.mockk
import kotlinx.datetime.toInstant

class CoverageAnalysisTest {

    private lateinit var coverageAnalysis: CoverageAnalysis
    private val dataStore: DataStore = mockk()
    private val exportTarget: ExportTarget = mockk(relaxed = true)
    private lateinit var expectation: Expectation
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()
    /*
    @BeforeEach
    fun setup() {
        expectation = AltitudeExpectation(10, "test", 1800)
        coverageAnalysis = CoverageAnalysis(expectation, 3600, dataStore, exportTarget, startTime, endTime)
    }

    @Test
    fun `calculateCoverage returns correct coverage when all expectations are met`() = runBlocking {
        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any()) } returns data

        val coverage = coverageAnalysis.calculateCoverage(startTime, endTime)

        assertEquals(1.0, coverage.absCoverage)
        assertEquals(1.0, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct coverage when no expectations are met`() = runBlocking {
        val data = createDataPoints(8, 10, 2.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any()) } returns data

        val coverage = coverageAnalysis.calculateCoverage(startTime, endTime)

        assertEquals(0.4, coverage.absCoverage)
        assertEquals(0.0, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct coverage when some expectations are met`() = runBlocking {
        val data = createDataPoints(15, 10, 1.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any()) } returns data

        val coverage = coverageAnalysis.calculateCoverage(startTime, endTime)

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

     */

}