package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

interface ExpectationAggregator {

    suspend fun aggregate(
        expectation: AggregateExpectation<*>,
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata>
}