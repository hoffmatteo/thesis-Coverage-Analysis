package carp.covanalyser.core.domain.factories

import carp.covanalyser.core.domain.ExportTarget

interface ExportTargetFactory {

    fun createExportTarget(type: String): ExportTarget
}