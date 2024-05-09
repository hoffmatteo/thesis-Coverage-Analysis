package carp.covanalyser.domain

abstract class AggregateExpectation<T : Expectation>(coverageAggregator: CoverageAggregator) : Expectation {
    open var expectations: MutableList<T> = mutableListOf()

}
