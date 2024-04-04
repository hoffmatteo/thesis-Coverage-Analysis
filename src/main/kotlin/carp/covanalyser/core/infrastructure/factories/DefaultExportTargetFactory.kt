package carp.covanalyser.core.infrastructure.factories

import carp.covanalyser.core.domain.ExportTarget
import carp.covanalyser.core.domain.factories.ExportTargetFactory
import carp.covanalyser.core.infrastructure.CSVExportTarget

class DefaultExportTargetFactory : ExportTargetFactory {

    override fun createExportTarget(type: String): ExportTarget {
        return when (type) {
            "CSVExportTarget" -> CSVExportTarget()
            else -> throw IllegalArgumentException("Unknown export target type: $type")
        }
    }
}