package carp.covanalyser.infrastructure.expectations

import carp.covanalyser.domain.DataTypeExpectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.Measurement
import kotlin.time.Duration

/**
 * An expectation for a generic data type.
 */
class GenericDataTypeExpectation(
    numDataPoints: Int,
    dataType: DataType,
    deviceName: String,
    duration: Duration,
    val isValidFunction: (Measurement<Data>) -> Boolean
) :
    DataTypeExpectation(
        numDataPoints, dataType,
        deviceName,
        duration
    ) {
    override fun isValid(input: Measurement<Data>): Boolean {
        return isValidFunction(input)
    }

}