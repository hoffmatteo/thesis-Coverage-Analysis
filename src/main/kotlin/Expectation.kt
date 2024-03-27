import carp.ws.domain.DataPoint
import dk.cachet.carp.common.application.tasks.Measure

class Expectation(
    var numDataPoints: Int,
    private var isValid: (input: DataPoint) -> Boolean,
    var dataSource: Measure,
    var timeframeSeconds: Int
) {


}


