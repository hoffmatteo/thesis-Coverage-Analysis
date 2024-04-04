package carp.covanalyser.core.infrastructure.factories

import carp.covanalyser.core.domain.Expectation
import carp.covanalyser.core.domain.factories.ExpectationFactory
import carp.covanalyser.core.infrastructure.AltitudeExpectation

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