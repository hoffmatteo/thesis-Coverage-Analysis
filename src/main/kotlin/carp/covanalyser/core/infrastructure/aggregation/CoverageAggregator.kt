package carp.covanalyser.core.infrastructure.aggregation

import carp.covanalyser.core.domain.Coverage

interface CoverageAggregator {

    fun aggregate(coverageList: List<Coverage>): Coverage
}