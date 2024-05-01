package carp.covanalyser.core.infrastructure.aggregation

import carp.covanalyser.core.domain.Coverage

class AverageCoverageAggregator : CoverageAggregator {
    override fun aggregate(coverageList: List<Coverage>): Coverage {
        // calculate the average of the coverage values
        val totalCoverageSum = coverageList.sumOf { it.absCoverage }
        val timeCoverageSum = coverageList.sumOf { it.timeCoverage }
        val averageTotalCoverage = totalCoverageSum / coverageList.size
        val averageTimeCoverage = timeCoverageSum / coverageList.size

        val startTime = coverageList.first().startTime
        val endTime = coverageList.first().endTime

        return Coverage(averageTotalCoverage, averageTimeCoverage, startTime, endTime)
    }
}