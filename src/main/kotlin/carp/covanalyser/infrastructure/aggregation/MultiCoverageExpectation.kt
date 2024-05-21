package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.CompositeExpectation
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.DataStore
import carp.covanalyser.domain.DataTypeExpectation
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class MultiCoverageExpectation : CompositeExpectation<DataTypeExpectation>(
) {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        //return all coverages
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (expectation in expectations) {
            val coverage = expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
            coverages.addAll(coverage)
        }
        return coverages
    }
}