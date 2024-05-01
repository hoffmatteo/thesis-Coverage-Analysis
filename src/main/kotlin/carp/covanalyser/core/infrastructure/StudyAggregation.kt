package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class StudyAggregation : AggregateExpectation<ProtocolAggregation>() {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage> {
        // return coverage per study
        return listOf(Coverage(0.0, 0.0, startTime, endTime))
    }
}