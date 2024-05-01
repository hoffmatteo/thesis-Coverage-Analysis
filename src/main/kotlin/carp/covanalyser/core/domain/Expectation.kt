package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant


interface Expectation {
    suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage>

}
