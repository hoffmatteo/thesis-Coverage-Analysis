package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.DataTypeExpectation

/**
 * Aggregates a list of [DataTypeExpectation]s of the same participant group to a single expectation.
 */
class ParticipantGroupAggregation(coverageAggregator: CoverageAggregator) :
    AggregateExpectation<DataTypeExpectation>(coverageAggregator, ExpectationAggregateExpectationStrategy()) {

    override fun getDescription(): String {
        return "ParticipantGroupAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}
