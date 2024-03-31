import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.infrastructure.AltitudeExpectation
import carp.ws.domain.DataPoint
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration


class CoverageAnalysis(
    var expectation: AltitudeExpectation,
    var timeFrameSeconds: Int
) {

    fun startCoverageAnalysis() {
        // while loop that requests data from given DataSource in timeFrameSeconds intervals and calculates coverage to export it
    }

    fun calculateCoverage(data: List<DataPoint>, startTime: Instant): Coverage {
        // obtain number of fulfilled expectations --> based on timestamp
        // split data into timeboxes as defined by expectation
        // Problem: timebox based on when data collection started --> first value could be a missing value
        // TODO find out when the data collection started
        val timeboxes = mutableListOf<List<DataPoint>>()
        var currEndTime = startTime.plus(expectation.timeframeSeconds.toLong().toDuration(DurationUnit.SECONDS))
        var currList = mutableListOf<DataPoint>()
        var fulfilledExpectations = 0
        // this code assumes that there are no multiple timeboxes missing --> wrong assumption
        for (item in data) {
            if (item.createdAt != null) {
                //TODO use createdAt?
                if (item.createdAt!! < currEndTime) {
                    currList.add(item)
                } else {
                    timeboxes.add(currList)
                    if (currList.size >= expectation.numDataPoints) {
                        fulfilledExpectations++
                    }
                    currEndTime =
                        currEndTime.plus(expectation.timeframeSeconds.toLong().toDuration(DurationUnit.SECONDS))
                    currList = mutableListOf<DataPoint>()
                    currList.add(item)
                }
            }
        }
        timeboxes.add(currList)
        if (currList.size >= expectation.numDataPoints) {
            fulfilledExpectations++
        }


        println(timeboxes)

        return Coverage(timeboxes, fulfilledExpectations / timeboxes.size.toDouble())


        // for each timebox, check if the number of valid data points is greater >= than the expectation
        // calculate proportion of fulfilled expectations
        // return proportion


    }
}