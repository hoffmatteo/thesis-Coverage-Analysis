package carp.covanalyser.core.domain

interface IExportTarget {

    fun exportData(data: Any): Any
}