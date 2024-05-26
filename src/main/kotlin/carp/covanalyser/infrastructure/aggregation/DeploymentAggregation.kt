package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Aggregates each [Expectation] over the given deployments and returns an aggregated coverage per [Expectation].
 * @param coverageAggregator The [CoverageAggregator] to use for aggregating the coverage of each [Expectation].
 */
class DeploymentAggregation(private val coverageAggregator: CoverageAggregator) : CompositeExpectation<Expectation>() {

    /**
     * Calculate the average coverage over all [deploymentIDs] for each [Expectation].
     * @param startTime The start time of the coverage calculation.
     * @param endTime The end time of the coverage calculation.
     * @param deploymentIDs The unique identifiers of the deployments to calculate coverage for.
     * @param dataStore The [DataStore] to use for retrieving data.
     * @return A list of [CoverageWithMetadata] objects, each containing the aggregated coverage for each [Expectation].
     */
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverageList = mutableListOf<CoverageWithMetadata>()
        for (expectation in expectations) {
            val coverage = expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
            coverageList.add(
                CoverageWithMetadata(
                    coverageAggregator.aggregate(coverage.map { it.coverage }),
                    deploymentIDs,
                    getDescription()
                )
            )
        }
        return coverageList
    }

    override fun getDescription(): String {
        return "DeploymentAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}