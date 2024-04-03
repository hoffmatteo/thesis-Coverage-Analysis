package carp.covanalyser.core.domain

interface ExpectationFactory {
    fun createExpectation(type: String, numDataPoints: Int, dataSource: String, timeframeSeconds: Int): Expectation
}