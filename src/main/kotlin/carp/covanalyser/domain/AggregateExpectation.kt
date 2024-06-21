package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Represents an expectation that is composed of other [expectations].
 * Calculates a coverage value for each [Expectation] in [expectations] and returns a list of them.
 * @param T The type of [Expectation] this composite expectation is composed of.
 */
open class AggregateExpectation<T : Expectation>(
    private val aggregateExpectationStrategy: AggregateExpectationStrategy
) :
    Expectation {
    val expectations: MutableList<T> = mutableListOf()

    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata> {
        return aggregateExpectationStrategy.aggregate(this, startTime, endTime, deploymentIDs, dataStore)
    }

    override fun getDescription(): String {
        return "AggregateExpectation: " + expectations.joinToString("\n") { it.getDescription() }
    }
}
