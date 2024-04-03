package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Expectation
import dk.cachet.carp.common.application.data.Data

class AltitudeExpectation(
    override var numDataPoints: Int,
    override var dataSource: String,
    override var timeframeSeconds: Int
) : Expectation {

    override fun isValid(input: Data): Boolean {
        //return input.carpBody?.get("altitude") as Double > 0
        return true
    }


}


