package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.CompositeExpectation
import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Aggregates a single [ParticipantGroupAggregation] over all participant groups/deployments in a study
 */
class StudyAggregation(private val coverageAggregator: CoverageAggregator) :
    CompositeExpectation<ParticipantGroupAggregation>() {
    /**
     * Calculate the average coverage of the [ParticipantGroupAggregation] over all deployments.
     * @param startTime The start time of the coverage calculation.
     * @param endTime The end time of the coverage calculation.
     * @param deploymentIDs The unique identifiers of the deployments to calculate coverage for.
     * @param dataStore The [DataStore] to use for retrieving data.
     * @return A single [CoverageWithMetadata] object, containing the aggregated coverage for the study.
     */
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverage = expectations.first().calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
        println("StudyAggregation: $coverage")
        return listOf(
            CoverageWithMetadata(
                coverageAggregator.aggregate(coverage.map { it.coverage }),
                coverage.first().deploymentIds,
                getDescription()
            )
        )
    }

    override fun getDescription(): String {
        return "StudyAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}