package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

//TODO model this differently --> one thing is to aggregate over expectations, the other thing is to aggregate over deployments!
// maybe this should be two separate cases
class DataTypeAggregation(val coverageAggregator: CoverageAggregator) : AggregateExpectation<DataTypeExpectation>(
    coverageAggregator
) {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverage = expectations.first().calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
        println("DataTypeAggregation: $coverage")
        return listOf(
            CoverageWithMetadata(
                coverageAggregator.aggregate(coverage.map { it.coverage }),
                coverage.first().deploymentIds,
                getDescription()
            )
        )
    }
}