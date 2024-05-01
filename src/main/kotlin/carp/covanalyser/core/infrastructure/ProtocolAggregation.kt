package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.DataStore
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class ProtocolAggregation : AggregateExpectation<DeviceAggregation>() {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<Coverage> {
        val coverages = mutableListOf<Coverage>()
        for (deploymentID in deploymentIDs) {
            val deploymentAverages = mutableListOf<Coverage>()
            for (expectation in expectations) {
                // this is coverage for a single expectation for a single deployment
                val coverage =
                    expectation.calculateCoverage(startTime, endTime, listOf(deploymentID), dataStore).first()
                deploymentAverages.add(coverage)
            }
            //TODO average
            coverages.add(Coverage(0.0, 0.0, startTime, endTime))
        }
        //coverage per deployment of protocol
        return coverages
    }
}
