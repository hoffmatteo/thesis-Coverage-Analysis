package carp.covanalyser.core.domain

import carp.covanalyser.core.infrastructure.aggregation.CoverageAggregator

abstract class AggregateExpectation<T : Expectation>(coverageAggregator: CoverageAggregator) : Expectation {
    open var expectations: MutableList<T> = mutableListOf()

}
