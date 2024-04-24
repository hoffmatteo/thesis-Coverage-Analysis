package carp.covanalyser.core.application

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.Expectation
import carp.covanalyser.core.domain.ExpectationVisitor
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

class CoverageCalculator(private val visitor: ExpectationVisitor) {
    //TODO make correct call based on expectation type!
    fun calculate(
        expectation: Expectation,
        dataPoints: List<Measurement<Data>>,
        startTime: Instant,
        endTime: Instant
    ): Coverage {
        return expectation.accept(visitor, dataPoints, startTime, endTime)
    }

    fun calculate(
        expectation: AggregateExpectation,
        dataPoints: List<Measurement<Data>>,
        startTime: Instant,
        endTime: Instant
    ): Coverage {
        println("Calculating aggregate expectation")
        return expectation.accept(visitor, dataPoints, startTime, endTime)
    }
}