package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

interface Expectation {
    var numDataPoints: Int
    var dataSource: String
    var timeframeSeconds: Int


    fun isValid(input: Measurement<Data>): Boolean


}