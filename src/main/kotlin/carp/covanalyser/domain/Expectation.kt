package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Represents an expectation to calculate coverage against.
 */
interface Expectation {
    //TODO if steps to be exported too, simply add exportTarget here
    /**
     * Calculates the coverage for the given expectation.
     *
     * @param startTime The start time of the coverage calculation.
     * @param endTime The end time of the coverage calculation.
     * @param deploymentIDs The deployment IDs to calculate coverage for.
     * @param dataStore The [DataStore] to obtain data from.
     */
    suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): List<CoverageWithMetadata>

    /**
     * Returns a description of the expectation, used for [CoverageWithMetadata].
     */
    fun getDescription(): String

}
