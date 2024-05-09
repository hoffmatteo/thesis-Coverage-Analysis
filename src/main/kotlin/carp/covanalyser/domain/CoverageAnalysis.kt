package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant
import kotlin.time.Duration

class CoverageAnalysis(
    var expectation: Expectation,
    var timeBetweenCalculations: Duration,
    var deploymentIds: List<UUID>,
    var exportTarget: ExportTarget,
    var dataStore: DataStore,
    var startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): List<Coverage> {
        val coverage = expectation.calculateCoverage(calcStartTime, calcEndTime, deploymentIds, dataStore)
        exportTarget.exportCoverage(coverage)
        return coverage
    }
}