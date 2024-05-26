package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Aggregates a list of [DataTypeExpectation]s of the same device to a single expectation.
 */
class DeviceAggregation(val deviceRoleName: String, private val coverageAggregator: CoverageAggregator) :
    CompositeExpectation<DataTypeExpectation>() {
    /**
     * Calculate the average coverage over all [DataTypeExpectation]s for each deployment.
     * @param startTime The start time of the coverage calculation.
     * @param endTime The end time of the coverage calculation.
     * @param deploymentIDs The unique identifiers of the deployments to calculate coverage for.
     * @param dataStore The [DataStore] to use for retrieving data.
     * @return A list of [CoverageWithMetadata] objects, each containing the aggregated coverage for each deployment.
     */
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (deploymentID in deploymentIDs) {
            val deploymentAverages = mutableListOf<CoverageWithMetadata>()
            for (expectation in expectations) {
                // this is coverage for a single expectation for a single deployment
                val coverage =
                    expectation.calculateCoverage(startTime, endTime, listOf(deploymentID), dataStore).first()
                deploymentAverages.add(coverage)
            }
            val coverageAverage = coverageAggregator.aggregate(deploymentAverages.map { it.coverage })
            coverages.add(CoverageWithMetadata(coverageAverage, listOf(deploymentID), getDescription()))
        }
        //coverage for a device per deployment
        println("DeviceAggregation: $coverages")
        return coverages
    }

    override fun getDescription(): String {
        return "DeviceAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}