package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.CoverageAggregator

/**
 * Aggregates a single [ParticipantGroupAggregation] over all participant groups/deployments in a study
 */
class StudyAggregation(coverageAggregator: CoverageAggregator) :
    AggregateExpectation<ParticipantGroupAggregation>(DeploymentAggregateExpectationStrategy(coverageAggregator)) {

    override fun getDescription(): String {
        return "StudyAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}