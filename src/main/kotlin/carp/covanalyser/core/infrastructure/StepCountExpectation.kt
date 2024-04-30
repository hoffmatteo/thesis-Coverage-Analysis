package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataStore
import carp.covanalyser.core.domain.DataStreamExpectation
import dk.cachet.carp.common.application.data.CarpDataTypes
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class StepCountExpectation(
    numDataPoints: Int, deviceName: String, dataStore: DataStore, timeframeSeconds: Int
) : DataStreamExpectation(
    numDataPoints, CarpDataTypes.STEP_COUNT.type, deviceName, dataStore, timeframeSeconds
) {
    override fun isConformant(input: Measurement<Data>): Boolean {
        return true
    }
}