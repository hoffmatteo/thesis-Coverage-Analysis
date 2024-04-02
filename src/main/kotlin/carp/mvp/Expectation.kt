package carp.mvp

import carp.ws.domain.DataPoint


class Expectation(
    var numDataPoints: Int,
    var dataSource: String,
    var timeframeSeconds: Int,
    var isValid: (DataPoint) -> Boolean
)