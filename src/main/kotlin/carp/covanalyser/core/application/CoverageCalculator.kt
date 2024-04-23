package carp.covanalyser.core.application

import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.Expectation
import carp.covanalyser.core.domain.ExpectationVisitor
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class CoverageCalculator(private val visitor: ExpectationVisitor) {
    fun calculate(expectation: Expectation, dataPoints: List<Measurement<Data>>): Coverage {
        return expectation.accept(visitor, dataPoints)
    }
}