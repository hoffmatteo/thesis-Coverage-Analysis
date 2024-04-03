package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.ExportTarget
import carp.covanalyser.core.domain.ExportTargetFactory

class DefaultExportTargetFactory : ExportTargetFactory {

    override fun createExportTarget(type: String): ExportTarget {
        return when (type) {
            "CSVExportTarget" -> CSVExportTarget()
            else -> throw IllegalArgumentException("Unknown export target type: $type")
        }
    }
}