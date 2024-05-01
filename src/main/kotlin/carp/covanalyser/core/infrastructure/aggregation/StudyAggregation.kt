package carp.covanalyser.core.infrastructure.aggregation

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

//TODO it should only have one sub-expectation
class StudyAggregation(private val coverageAggregator: CoverageAggregator) : AggregateExpectation<ProtocolAggregation>(
    coverageAggregator
) {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage> {
        val coverage = expectations.first().calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
        println("StudyAggregation: $coverage")
        return listOf(coverageAggregator.aggregate(coverage))
    }
}