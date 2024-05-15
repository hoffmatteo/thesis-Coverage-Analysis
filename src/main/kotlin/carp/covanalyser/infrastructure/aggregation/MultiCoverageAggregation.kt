package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

//TODO good question: use case combining vs. just return multiple coverages! --> separate them?
class MultiCoverageAggregation(coverageAggregator: CoverageAggregator) : AggregateExpectation<DataTypeExpectation>(
    coverageAggregator
) {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage> {
        //return all coverages
        val coverages = mutableListOf<Coverage>()
        for (expectation in expectations) {
            val coverage = expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
            coverages.addAll(coverage)
        }
        return coverages
    }
}