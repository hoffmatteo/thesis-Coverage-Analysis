package carp.covanalyser.domain

//TODO could be nice to allow any type of additional meta data here?
interface ExportTarget {
    //TODO metadata, version of code (git commit), ..., time period, analysis id
    suspend fun exportCoverage(data: List<Coverage>): String
}