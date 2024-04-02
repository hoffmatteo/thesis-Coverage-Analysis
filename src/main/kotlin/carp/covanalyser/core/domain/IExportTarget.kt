package carp.covanalyser.core.domain

interface IExportTarget {

    suspend fun exportCoverage(data: Coverage): String
}