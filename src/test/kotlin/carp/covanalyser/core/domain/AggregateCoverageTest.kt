package carp.covanalyser.core.domain

import carp.covanalyser.domain.DataStore
import carp.covanalyser.infrastructure.aggregation.AverageCoverageAggregator
import carp.covanalyser.infrastructure.aggregation.ParticipantGroupAggregation
import carp.covanalyser.infrastructure.aggregation.StudyAggregation
import carp.covanalyser.infrastructure.expectations.LocationExpectation
import carp.covanalyser.infrastructure.expectations.StepCountExpectation
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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class AggregateCoverageTest {

    private lateinit var participantGroupAggregation: ParticipantGroupAggregation
    private lateinit var studyAggregation: StudyAggregation
    private val dataStore: DataStore = mockk()
    private val startTime = "2022-01-01T00:00:00Z".toInstant()
    private val endTime = "2022-01-01T01:00:00Z".toInstant()

    @BeforeEach
    fun setup() {
        val locationExpectation = LocationExpectation(1, "phone", 30.toDuration(DurationUnit.MINUTES))
        val stepCountExpectation = StepCountExpectation(10, "phone", 30.toDuration(DurationUnit.MINUTES))

        val coverageAggregator = AverageCoverageAggregator()

        participantGroupAggregation = ParticipantGroupAggregation(coverageAggregator)
        participantGroupAggregation.expectations.addAll(
            listOf(locationExpectation, stepCountExpectation),
        )

        studyAggregation = StudyAggregation(coverageAggregator)
        studyAggregation.expectations.add(participantGroupAggregation)
    }

    @Test
    fun `calculateCoverage returns correct participant coverage when all expectations are met`() = runBlocking {
        val data = createDataPoints(40, 10, 2.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            participantGroupAggregation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertTrue(coverage.absCoverage >= 1.0)
        assertTrue(coverage.expectationCoverage >= 1.0)
    }

    @Test
    fun `calculateCoverage returns correct participant coverage when some expectations are met`() = runBlocking {
        val data = createDataPoints(2, 10, 30.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            participantGroupAggregation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertEquals(0.55, coverage.absCoverage)
        assertEquals(0.5, coverage.expectationCoverage)
    }

    @Test
    fun `calculateCoverage returns correct participant coverage when no expectations are met`() = runBlocking {
        val data = createDataPoints(0, 0, 30.toDuration(DurationUnit.MINUTES), startTime)
        coEvery { dataStore.obtainData(any(), any(), any()) } returns data

        val coverage =
            participantGroupAggregation.calculateCoverage(startTime, endTime, listOf(UUID.randomUUID()), dataStore)
                .first().coverage

        assertEquals(0.0, coverage.absCoverage)
        assertEquals(0.0, coverage.expectationCoverage)
    }

    @Test
    fun `calculateCoverage returns correct study coverage when all expectations are met`() = runBlocking {
        val deploymentIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val data = createDataPointsForDeployments(
            mapOf(deploymentIds[0] to 40, deploymentIds[1] to 40),
            10,
            2.toDuration(DurationUnit.MINUTES),
            startTime
        )
        deploymentIds.forEach { deploymentId ->
            coEvery {
                dataStore.obtainData(
                    any(),
                    any(),
                    match { it.studyDeploymentId == deploymentId }
                )
            } returns data[deploymentId]!!
        }

        val coverage = studyAggregation.calculateCoverage(startTime, endTime, deploymentIds, dataStore)
            .first().coverage

        assertTrue(coverage.absCoverage >= 1.0)
        assertTrue(coverage.expectationCoverage >= 1.0)
    }

    @Test
    fun `calculateCoverage returns correct study coverage when some expectations are met`() = runBlocking {
        val deploymentIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val data = createDataPointsForDeployments(
            mapOf(deploymentIds[0] to 2, deploymentIds[1] to 10),
            10,
            30.toDuration(DurationUnit.MINUTES),
            startTime
        )
        deploymentIds.forEach { deploymentId ->
            coEvery {
                dataStore.obtainData(
                    any(),
                    any(),
                    match { it.studyDeploymentId == deploymentId })
            } returns data[deploymentId]!!
        }


        val coverage = studyAggregation.calculateCoverage(startTime, endTime, deploymentIds, dataStore)
            .first().coverage

        assertEquals(0.55, coverage.absCoverage)
        assertEquals(0.5, coverage.expectationCoverage)
    }

    @Test
    fun `calculateCoverage returns correct study coverage when no expectations are met`() = runBlocking {
        val deploymentIds = listOf(UUID.randomUUID(), UUID.randomUUID())
        val data = createDataPointsForDeployments(
            mapOf(deploymentIds[0] to 0, deploymentIds[1] to 0),
            0,
            30.toDuration(DurationUnit.MINUTES),
            startTime
        )
        deploymentIds.forEach { deploymentId ->
            coEvery {
                dataStore.obtainData(
                    any(),
                    any(),
                    match { it.studyDeploymentId == deploymentId })

            } returns data[deploymentId]!!
        }

        val coverage = studyAggregation.calculateCoverage(startTime, endTime, deploymentIds, dataStore)
            .first().coverage

        assertEquals(0.0, coverage.absCoverage)
        assertEquals(0.0, coverage.expectationCoverage)
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

    private fun createDataPointsForDeployments(
        objectsPerDeployment: Map<UUID, Int>,
        stepCount: Int,
        duration: Duration,
        startTime: Instant
    ): Map<UUID, List<Measurement<Data>>> {
        val multipleDeploymentsData = HashMap<UUID, List<Measurement<Data>>>()
        objectsPerDeployment.forEach { (deploymentId, numberOfObjects) ->
            multipleDeploymentsData[deploymentId] = createDataPoints(numberOfObjects, stepCount, duration, startTime)
        }
        return multipleDeploymentsData
    }
}