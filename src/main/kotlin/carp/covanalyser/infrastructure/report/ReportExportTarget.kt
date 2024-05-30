package carp.covanalyser.infrastructure.report

import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.ExportTarget
import carp.covanalyser.infrastructure.CSVExportTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ReportExportTarget(val csvName: String) : ExportTarget {
    override suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean {
        CSVExportTarget(csvName).exportCoverage(data, coverageAnalysis)

        val processBuilder =
            ProcessBuilder("node", "src/main/kotlin/carp/covanalyser/infrastructure/report/ExportToPDF.js")
        processBuilder.inheritIO()

        try {
            val process = withContext(Dispatchers.IO) {
                processBuilder.start()
            }
            val exitCode = withContext(Dispatchers.IO) {
                process.waitFor()
            }
            if (exitCode == 0) {
                println("PDF generated successfully.")
            } else {
                println("Error generating PDF. Exit code: $exitCode")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }
}