package carp.covanalyser.domain

interface CoverageAggregator {
    //TODO take collection and function, apply function to list
    fun aggregate(coverageList: List<Coverage>): Coverage
}