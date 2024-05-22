package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

// aggregates each expectation over the given deployments
class DeploymentAggregation(private val coverageAggregator: CoverageAggregator) : CompositeExpectation<Expectation>() {

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