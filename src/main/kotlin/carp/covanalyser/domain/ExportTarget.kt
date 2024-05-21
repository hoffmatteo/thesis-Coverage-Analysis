package carp.covanalyser.domain

//TODO could be nice to allow any type of additional meta data here?
interface ExportTarget {
    suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean
}