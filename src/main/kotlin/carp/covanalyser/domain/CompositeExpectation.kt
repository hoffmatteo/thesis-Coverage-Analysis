package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

open class CompositeExpectation<T : Expectation> : Expectation {
    open var expectations: MutableList<T> = mutableListOf()
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverages = mutableListOf<CoverageWithMetadata>()
        for (expectation in expectations) {
            val coverage = expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
            coverages.addAll(coverage)
        }
        return coverages
    }

    override fun getDescription(): String {
        return "CompositeExpectation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}