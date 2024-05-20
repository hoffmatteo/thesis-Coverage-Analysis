package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

abstract class AggregateExpectation<T : Expectation>(coverageAggregator: CoverageAggregator) :
    CompositeExpectation<T>() {

    abstract override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage>
}
