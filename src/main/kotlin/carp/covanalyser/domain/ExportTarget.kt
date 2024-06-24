package carp.covanalyser.domain

/**
 * Represents a target to export [CoverageWithMetadata] data to.
 */
interface ExportTarget {
    /**
     * Exports the given [data] to the target.
     * Returns true if the export was successful, false otherwise.
     * @param data The [CoverageWithMetadata] data to export.
     * @param coverageAnalysis The [CoverageAnalysis] to export data for.
     */
    suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean
}
