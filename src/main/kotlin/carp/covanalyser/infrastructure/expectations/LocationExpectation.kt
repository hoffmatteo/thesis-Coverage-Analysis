package carp.covanalyser.infrastructure.expectations

import carp.covanalyser.domain.DataTypeExpectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.Measurement
import kotlin.time.Duration

class LocationExpectation(
    numDataPoints: Int, deviceName: String, duration: Duration

) : DataTypeExpectation(
    numDataPoints, DataType("dk.cachet.carp", "location"), deviceName, duration
) {
    override fun isValid(input: Measurement<Data>): Boolean {
        return true
    }

}