package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Expectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class AltitudeExpectation(
    override var numDataPoints: Int,
    override var dataSource: String,
    override var timeframeSeconds: Int
) : Expectation {

    override fun asExpected(input: Measurement<Data>): Boolean {
        return true
    }


}


