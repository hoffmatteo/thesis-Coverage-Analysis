package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant
import kotlin.time.Duration

class CoverageAnalysis(
    val id: UUID = UUID.randomUUID(),
    val expectation: Expectation,
    val timeBetweenCalculations: Duration,
    val deploymentIds: List<UUID>,
    val exportTarget: ExportTarget,
    val dataStore: DataStore,
    val startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): List<Coverage> {
        val coverage = expectation.calculateCoverage(calcStartTime, calcEndTime, deploymentIds, dataStore)
        exportTarget.exportCoverage(coverage, this)
        return coverage
    }
}