package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.DataTypeExpectation

/**
 * Aggregates a list of [DataTypeExpectation]s of the same device to a single expectation.
 */
class DeviceAggregation(coverageAggregator: CoverageAggregator) :
    AggregateExpectation<DataTypeExpectation>(coverageAggregator, ExpectationAggregateExpectationStrategy()) {

    override fun getDescription(): String {
        return "DeviceAggregation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}