import carp.ws.domain.DataPoint

class Expectation(
    var numDataPoints: Int,
    private var isValid: (input: DataPoint) -> Boolean,
    var dataSource: String,
    var timeframeSeconds: Int
) {


}


