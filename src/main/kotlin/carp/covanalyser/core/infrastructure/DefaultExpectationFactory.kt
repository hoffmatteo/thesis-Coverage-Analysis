package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Expectation
import carp.covanalyser.core.domain.ExpectationFactory

class DefaultExpectationFactory : ExpectationFactory {

    override fun createExpectation(
        type: String,
        numDataPoints: Int,
        dataSource: String,
        timeframeSeconds: Int
    ): Expectation {
        return when (type) {
            "AltitudeExpectation" -> AltitudeExpectation(numDataPoints, dataSource, timeframeSeconds)
            // Add more types as needed
            else -> throw IllegalArgumentException("Invalid expectation type: $type")
        }
    }

}