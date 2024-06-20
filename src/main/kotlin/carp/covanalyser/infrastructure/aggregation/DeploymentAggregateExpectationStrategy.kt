package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.AggregateExpectationStrategy
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Aggregates over deployments.
 */
class DeploymentAggregateExpectationStrategy : AggregateExpectationStrategy {
    override suspend fun aggregate(
        expectation: AggregateExpectation<*>,
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverageList = mutableListOf<CoverageWithMetadata>()
        for (subExpectation in expectation.expectations) {
            val coverage = subExpectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
            coverageList.add(
                CoverageWithMetadata(
                    expectation.coverageAggregator.aggregate(coverage.map { it.coverage }),
                    deploymentIDs,
                    expectation.getDescription(),
                    coverage
                )
            )
        }
        return coverageList
    }
}