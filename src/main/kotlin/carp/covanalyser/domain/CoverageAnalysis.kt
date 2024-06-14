package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant
import kotlin.time.Duration

/**
 * Represents an instance of a concrete coverage analysis.
 * @param id The unique identifier of the coverage analysis.
 * @param expectation The [Expectation] to calculate coverage against.
 * @param timeBetweenCalculations The time between each calculation of coverage.
 * @param deploymentIds The deployment IDs to calculate coverage for.
 * @param exportTarget The [ExportTarget] where calculated coverage is exported to.
 * @param dataStore The [DataStore] to obtain data from.
 * @param startTime The start time of the coverage analysis.
 * @param endTime The end time of the coverage analysis.
 */
class CoverageAnalysis(
    val id: UUID = UUID.randomUUID(),
    val expectations: List<Expectation>,
    val timeBetweenCalculations: Duration,
    val deploymentIds: List<UUID>,
    val exportTarget: ExportTarget,
    val dataStore: DataStore,
    val startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    /**
     * Calculates the coverage using the given [Expectation] and exports it to the [ExportTarget].
     *
     * @param calcStartTime The start time of the coverage calculation.
     * @param calcEndTime The end time of the coverage calculation.
     */
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant) {
        for (expectation in expectations) {
            val coverage = expectation.calculateCoverage(calcStartTime, calcEndTime, deploymentIds, dataStore)
            exportTarget.exportCoverage(coverage, this)
        }
    }
}