package carp.covanalyser.domain

interface ExportTarget {
    suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean
}