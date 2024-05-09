package carp.covanalyser.domain

interface ExportTarget {
    //TODO metadata, version of code (git commit), ..., time period, analysis id
    suspend fun exportCoverage(data: List<Coverage>): String
}