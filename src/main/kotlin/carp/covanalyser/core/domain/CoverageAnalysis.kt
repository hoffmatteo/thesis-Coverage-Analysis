package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class CoverageAnalysis(
    var expectation: Expectation,
    var timeFrameSeconds: Int,
    var deploymentIds: List<UUID>,
    var exportTarget: ExportTarget,
    var dataStore: DataStore,
    var startTime: Instant,
    //TODO endTime could be left open? Or make it mandatory?
    var endTime: Instant
) {
    suspend fun calculateCoverage(calcStartTime: Instant, calcEndTime: Instant): Coverage {
        val coverage = expectation.calculateCoverage(calcStartTime, calcEndTime, deploymentIds, dataStore)
        exportTarget.exportCoverage(coverage)
        return coverage
    }
}