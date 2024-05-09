package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.Coverage
import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class StudyAggregation(private val coverageAggregator: CoverageAggregator) :
    AggregateExpectation<ParticipantGroupAggregation>(
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