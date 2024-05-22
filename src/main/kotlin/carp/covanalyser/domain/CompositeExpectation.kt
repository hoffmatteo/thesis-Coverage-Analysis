package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

open class CompositeExpectation<T : Expectation> :
    Expectation {
    val expectations: MutableList<T> = mutableListOf()

    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        val coverageList = mutableListOf<CoverageWithMetadata>()
        for (expectation in expectations) {
            coverageList.addAll(expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore))
        }
        return coverageList
    }

    override fun getDescription(): String {
        return "CompositeExpectation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}
