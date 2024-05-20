package carp.covanalyser.infrastructure.report

import carp.covanalyser.domain.Coverage
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.ExportTarget
import carp.covanalyser.infrastructure.CSVExportTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportExportTarget(val csvName: String) : ExportTarget {
    override suspend fun exportCoverage(data: List<Coverage>, coverageAnalysis: CoverageAnalysis): String {
        CSVExportTarget(csvName).exportCoverage(data, coverageAnalysis)

        val processBuilder =
            ProcessBuilder("node", "src/main/kotlin/carp/covanalyser/infrastructure/report/ExportToPDF.js")
        processBuilder.inheritIO()
        val process = withContext(Dispatchers.IO) {
            processBuilder.start()
        }
        withContext(Dispatchers.IO) {
            process.waitFor()
        }
        return ""
    }
}