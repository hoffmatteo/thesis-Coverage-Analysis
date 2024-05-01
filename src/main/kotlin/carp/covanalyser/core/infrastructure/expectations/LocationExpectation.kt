package carp.covanalyser.core.infrastructure.expectations

import carp.covanalyser.core.domain.DataStreamExpectation
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlin.time.Duration

class LocationExpectation(
    numDataPoints: Int, deviceName: String, duration: Duration

) : DataStreamExpectation(
    numDataPoints, CarpDataTypes.GEOLOCATION.type, deviceName, duration
) {
    override fun isConformant(input: Measurement<Data>): Boolean {
        return true
    }

}