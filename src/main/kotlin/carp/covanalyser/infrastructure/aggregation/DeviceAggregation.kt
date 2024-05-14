package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

//TODO device group, e.g. all smart watches
class DeviceAggregation(deviceRoleName: String, private val coverageAggregator: CoverageAggregator) :
    AggregateExpectation<DataTypeExpectation>(
        coverageAggregator
    ) {
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
            coverages.add(coverageAggregator.aggregate(deploymentAverages))
        }
        //coverage for a device per deployment
        println("DeviceAggregation: $coverages")
        return coverages
    }
}