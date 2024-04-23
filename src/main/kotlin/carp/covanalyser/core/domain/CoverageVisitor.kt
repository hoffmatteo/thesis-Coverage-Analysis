package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

class CoverageVisitor : ExpectationVisitor {
    override fun visit(expectation: Expectation, data: List<Measurement<Data>>): Coverage {
        TODO("Not yet implemented")
        // calculate coverage of data with expectation
        //return Coverage(0.0)
    }

    override fun visit(expectation: AggregateExpectation, data: List<Measurement<Data>>): Coverage {
        TODO("Not yet implemented")
        /*
        var coverage = 0.0
        for (e in expectation.expectations) {
            coverage += e.accept(this, data)
        }
        return coverage / expectation.expectations.size

         */
    }

}