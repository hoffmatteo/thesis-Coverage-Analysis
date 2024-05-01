package carp.covanalyser.core.domain

import carp.covanalyser.core.infrastructure.aggregation.*
import carp.covanalyser.core.infrastructure.expectations.LocationExpectation
import carp.covanalyser.core.infrastructure.expectations.StepCountExpectation
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
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class AggregateCoverageTest {


    private lateinit var dataStreamExpectations: List<DataStreamExpectation>
    private lateinit var deviceAggregations: List<DeviceAggregation>
    private lateinit var protocolAggregation: ProtocolAggregation
    private lateinit var studyAggregation: StudyAggregation
    private lateinit var coverageAnalysis: CoverageAnalysis
    private val dataStore: DataStore = mockk()
    private val exportTarget: ExportTarget = mockk(relaxed = true)
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()

    @BeforeEach
    fun setup() {
        val locationExpectation = LocationExpectation(1, "phone", 30.toDuration(DurationUnit.MINUTES))
        val stepCountExpectation = StepCountExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))

        val coverageAggregator = AverageCoverageAggregator()

        deviceAggregations =
            AggregationFactory().createDeviceAggregations(
                listOf(locationExpectation, stepCountExpectation),
                coverageAggregator
            )

        protocolAggregation = ProtocolAggregation(coverageAggregator)
        protocolAggregation.expectations.addAll(deviceAggregations)

        studyAggregation = StudyAggregation(coverageAggregator)
        studyAggregation.expectations.add(protocolAggregation)

    }

    @Test
    fun `calculateCoverage returns correct device coverage when all expectations are met`() = runBlocking {
        coverageAnalysis = CoverageAnalysis(
            deviceAggregations.first(),
            1.toDuration(DurationUnit.HOURS),
            listOf(
                UUID.randomUUID()
            ),
            exportTarget,
            dataStore,
            startTime,
            endTime
        )

        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage = coverageAnalysis.calculateCoverage(startTime, endTime).first()

        assertEquals(1.0, coverage.absCoverage)
        assertEquals(1.0, coverage.timeCoverage)
    }

    @Test
    fun `calculateCoverage returns correct device coverage when some expectations are met`() = runBlocking {
        coverageAnalysis = CoverageAnalysis(
            deviceAggregations.first(),
            1.toDuration(DurationUnit.HOURS),
            listOf(
                UUID.randomUUID()
            ),
            exportTarget,
            dataStore,
            startTime,
            endTime
        )

        val data = createDataPoints(2, 10, 30.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage = coverageAnalysis.calculateCoverage(startTime, endTime).first()

        assertEquals(0.55, coverage.absCoverage)
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