package carp.covanalyser.core.domain

interface ExportTargetFactory {

    fun createExportTarget(type: String): ExportTarget
}