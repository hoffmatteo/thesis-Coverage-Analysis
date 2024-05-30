package carp.covanalyser.infrastructure.report

import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.ExportTarget

class PythonExportTarget : ExportTarget {
    override suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean {
        //TODO using python script, generate report for entire study
        return false
    }
}