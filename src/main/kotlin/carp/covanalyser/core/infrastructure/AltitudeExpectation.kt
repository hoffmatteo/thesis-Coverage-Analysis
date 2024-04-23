package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Expectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class AltitudeExpectation(numDataPoints: Int, dataSource: String, timeframeSeconds: Int) : Expectation(
    numDataPoints, dataSource,
    timeframeSeconds
) {


    override fun dataIsConformant(input: Measurement<Data>): Boolean {
        return true
    }


}


