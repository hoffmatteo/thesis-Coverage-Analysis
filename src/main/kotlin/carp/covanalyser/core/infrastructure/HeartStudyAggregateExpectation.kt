package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Expectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class HeartStudyAggregateExpectation(numDataPoints: Int, dataSource: String, timeframeSeconds: Int) :
    AggregateExpectation(
        numDataPoints,
        dataSource, timeframeSeconds
    ) {
    override var expectations: List<Expectation> =
        mutableListOf(AltitudeExpectation(1, "test", 1), AltitudeExpectation(1, "test", 1))

    override fun dataIsConformant(input: Measurement<Data>): Boolean {
        return true
    }
}