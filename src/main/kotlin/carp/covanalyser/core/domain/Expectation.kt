package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

abstract class Expectation(
    var numDataPoints: Int,
    var deviceRoleName: String,
    var timeframeSeconds: Int
) {

    abstract fun dataIsConformant(input: Measurement<Data>): Boolean

    fun accept(
        visitor: ExpectationVisitor,
        data: List<Measurement<Data>>,
        startTime: Instant,
        endTime: Instant
    ): Coverage {
        return visitor.visit(this, data, startTime, endTime)
    }


}