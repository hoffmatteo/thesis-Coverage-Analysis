package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

abstract class AggregateExpectation : Expectation {
    val expectations: List<Expectation> = ArrayList()

    override fun isSatisified(input: Measurement<Data>, startTime: Instant, endTime: Instant): Int {
        // example: looking at daily coverage of 3 sensors:
        // StepCount hourly, Heart Rate every 5 minutes, and glucose every 30minutes
        // 24 step count expectations, 288 heart rate expectations, 48 glucose expectations
        // 360 expectations in total
        // every expectation: return number of fulfilled expectations and number of total expectations?
        // pass it data and timeframe --> returns things above
        var returnVal = 0
        for (expectation in expectations) {
            returnVal += expectation.isSatisified(input, startTime, endTime)
        }
        return returnVal
    }
}