package carp.covanalyser.core.domain

interface ExportTarget {

    suspend fun exportCoverage(data: Coverage): String
}