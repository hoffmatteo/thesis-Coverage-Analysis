package carp.covanalyser.infrastructure

import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageWithMetadata
import carp.covanalyser.domain.ExportTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVExportTarget(private val filepath: String) : ExportTarget {
    init {
        val file = File(filepath)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val writer = FileWriter(file)
        val printer = CSVFormat.DEFAULT.print(writer)
        printer.printRecord(
            "Start Time",
            "End Time",
            "Absolute Coverage",
            "Expectation Coverage",
            "Deployment IDs",
            "Description"
        )
        printer.close()

    }

    override suspend fun exportCoverage(data: List<CoverageWithMetadata>, coverageAnalysis: CoverageAnalysis): Boolean {
        println("Exporting coverage data to CSV file for $coverageAnalysis")
        val file = File(filepath)
        var printer: CSVPrinter? = null

        try {
            val writer = withContext(Dispatchers.IO) {
                FileWriter(file, true)
            } // Open in append mode
            printer = CSVFormat.DEFAULT.print(writer)
            for (coverageWithMetadata in data) {
                // Append the new row of coverage data
                printer.printRecord(
                    toReadableString(coverageWithMetadata.coverage.startTime),
                    toReadableString(coverageWithMetadata.coverage.endTime),
                    coverageWithMetadata.coverage.absCoverage,
                    coverageWithMetadata.coverage.expectationCoverage,
                    coverageWithMetadata.deploymentIds.joinToString { it.toString() },
                    coverageWithMetadata.description
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                printer?.flush()
                printer?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return true
    }

    private fun toReadableString(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.toString()

    }
}




