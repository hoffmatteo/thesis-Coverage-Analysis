package carp.covanalyser.core.infrastructure.expectations

import carp.covanalyser.core.domain.DataStreamExpectation
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlin.time.Duration

class StepCountExpectation(
    numDataPoints: Int, deviceName: String, duration: Duration
) : DataStreamExpectation(
    numDataPoints, CarpDataTypes.STEP_COUNT.type, deviceName, duration
) {
    override fun isConformant(input: Measurement<Data>): Boolean {
        return true
    }
}