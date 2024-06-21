package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Aggregates over expectations.
 */
class ExpectationAggregateExpectationStrategy(override var coverageAggregator: CoverageAggregator) :
    AggregateExpectationStrategy {
    override suspend fun aggregate(
        expectation: AggregateExpectation<*>,
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (deploymentID in deploymentIDs) {
            val deploymentAverages = mutableListOf<CoverageWithMetadata>()
            for (subExpectation in expectation.expectations) {
                // this is coverage for a single expectation for a single deployment
                val coverage =
                    subExpectation.calculateCoverage(startTime, endTime, listOf(deploymentID), dataStore).first()
                deploymentAverages.add(coverage)
            }
            val coverageAverage = coverageAggregator.aggregate(deploymentAverages.map { it.coverage })
            coverages.add(
                CoverageWithMetadata(
                    coverageAverage,
                    listOf(deploymentID),
                    expectation.getDescription(),
                    deploymentAverages
                )
            )
        }
        return coverages

    }
}