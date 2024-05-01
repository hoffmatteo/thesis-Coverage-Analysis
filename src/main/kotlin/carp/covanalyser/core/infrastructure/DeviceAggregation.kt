package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.AggregateExpectation
import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.DataStore
import carp.covanalyser.core.domain.DataStreamExpectation
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class DeviceAggregation(deviceRoleName: String) : AggregateExpectation<DataStreamExpectation>() {
    //TODO aggregate different data streams from a single device into a single coverage metric (still per deployment)
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
        //coverage for a device per deployment
        return coverages
    }
}