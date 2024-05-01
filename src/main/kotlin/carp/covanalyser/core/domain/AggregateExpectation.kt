package carp.covanalyser.core.domain

abstract class AggregateExpectation<T : Expectation> : Expectation {
    open var expectations: MutableList<T> = mutableListOf()

}
