package carp.covanalyser.domain

//TODO could be nice to allow any type of additional meta data here?
interface ExportTarget {
    suspend fun exportCoverage(data: List<Coverage>, coverageAnalysis: CoverageAnalysis): String
}