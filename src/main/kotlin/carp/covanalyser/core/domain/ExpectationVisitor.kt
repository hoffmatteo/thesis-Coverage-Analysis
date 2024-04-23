package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

interface ExpectationVisitor {
    fun visit(expectation: Expectation, data: List<Measurement<Data>>): Coverage
    fun visit(expectation: AggregateExpectation, data: List<Measurement<Data>>): Coverage
}
