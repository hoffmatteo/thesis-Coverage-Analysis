package carp.covanalyser.core.domain.factories

import carp.covanalyser.core.domain.Expectation

interface ExpectationFactory {
    fun createExpectation(type: String, numDataPoints: Int, dataSource: String, timeframeSeconds: Int): Expectation
}