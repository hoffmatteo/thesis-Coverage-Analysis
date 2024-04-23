package carp.covanalyser.core.domain

abstract class AggregateExpectation(numDataPoints: Int, dataSource: String, timeframeSeconds: Int) : Expectation(
    numDataPoints, dataSource,
    timeframeSeconds
) {
    val expectations: List<Expectation> = ArrayList()

}