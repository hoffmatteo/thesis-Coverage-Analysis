package carp.covanalyser.domain

/**
 * Aggregates multiple [Coverage] values into a single one.
 */
interface CoverageAggregator {
    fun aggregate(coverageList: List<Coverage>): Coverage
}