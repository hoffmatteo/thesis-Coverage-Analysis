package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.Coverage
import carp.covanalyser.domain.CoverageAggregator

/**
 * Aggregates a list of [Coverage] objects by calculating the average of their coverage values.
 */
class AverageCoverageAggregator : CoverageAggregator {
    override fun aggregate(coverageList: List<Coverage>): Coverage {
        // calculate the average of the coverage values
        val totalCoverageSum = coverageList.sumOf { it.absCoverage }
        val timeCoverageSum = coverageList.sumOf { it.expectationCoverage }
        val averageTotalCoverage = totalCoverageSum / coverageList.size
        val averageTimeCoverage = timeCoverageSum / coverageList.size

        val startTime = coverageList.first().startTime
        val endTime = coverageList.first().endTime
        return Coverage(averageTotalCoverage, averageTimeCoverage, startTime, endTime)
    }
}
