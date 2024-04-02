package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data

interface IExpectation {
    var numDataPoints: Int
    var dataSource: String
    var timeframeSeconds: Int


    fun isValid(input: Data): Boolean


}