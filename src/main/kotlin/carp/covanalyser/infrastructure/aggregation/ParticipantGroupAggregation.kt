package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.*
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

//TODO just keep it to data streams
class ParticipantGroupAggregation(private val coverageAggregator: CoverageAggregator) :
    AggregateExpectation<DataTypeExpectation>(
        coverageAggregator
    ) {
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (deploymentID in deploymentIDs) {
            val deploymentAverages = mutableListOf<CoverageWithMetadata>()
            for (expectation in expectations) {
                // this is coverage for a single expectation for a single deployment
                val coverage =
                    expectation.calculateCoverage(startTime, endTime, listOf(deploymentID), dataStore).first()
                deploymentAverages.add(coverage)
            }
            val coverageAverage = coverageAggregator.aggregate(deploymentAverages.map { it.coverage })
            coverages.add(CoverageWithMetadata(coverageAverage, deploymentIDs, getDescription()))
        }
        //coverage per deployment of protocol
        println("ProtocolAggregation: $coverages")
        return coverages
    }
}
