import carp.ws.domain.DataPoint
import java.time.Instant


class Coverage(
    var expectation: Expectation,
    var timeFrameSeconds: Int
) {

    fun calculateCoverage(data: List<DataPoint>, startTime: Instant): Double {
        // obtain number of fulfilled expectations --> based on timestamp
        // split data into timeboxes as defined by expectation
        // Problem: timebox based on when data collection started --> first value could be a missing value
        /*
        val timeboxes = mutableListOf<List<DataPoint>>()
        var currEndTime = startTime.plusSeconds(expectation.timeframeSeconds.toLong())
        var currList = mutableListOf<DataPoint>()
        // this code assumes that there are no multiple timeboxes missing --> wrong assumption
        for (item in data) {
            if (item.createdAt != null) {
                //TODO use createdAt?
                if (item.createdAt!!.isBefore(currEndTime)) {
                    currList.add(item)
                } else {
                    timeboxes.add(currList)
                    currEndTime = currEndTime.plusSeconds(expectation.timeframeSeconds.toLong())
                    currList = mutableListOf<DataPoint>()
                    currList.add(item)
                }
            }
        }
        timeboxes.add(currList)

        println(timeboxes)

        // for each timebox, check if the number of valid data points is greater >= than the expectation
        // calculate proportion of fulfilled expectations
        // return proportion

        */
        return 0.0

    }
}