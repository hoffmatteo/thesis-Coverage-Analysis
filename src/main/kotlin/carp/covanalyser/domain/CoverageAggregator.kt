package carp.covanalyser.domain

interface CoverageAggregator {
    fun aggregate(coverageList: List<Coverage>): Coverage
}