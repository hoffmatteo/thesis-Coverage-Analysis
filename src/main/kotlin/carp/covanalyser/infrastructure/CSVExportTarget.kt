package carp.covanalyser.infrastructure

import carp.covanalyser.domain.Coverage
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

class CSVExportTarget(val filepath: String) : ExportTarget {
    init {
        //TODO change
        val file = File(filepath)
        if (file.exists()) {
            file.delete()
        }
        file.createNewFile()
        val writer = FileWriter(file)
        val printer = CSVFormat.DEFAULT.print(writer)
        printer.printRecord("Start Time", "End Time", "Absolute Coverage", "Time Coverage", "Data Type")
        printer.close()

    }

    override suspend fun exportCoverage(data: List<Coverage>): String {
        println("Exporting coverage data to CSV file")
        val file = File(filepath)
        var printer: CSVPrinter? = null

        try {
            val writer = withContext(Dispatchers.IO) {
                FileWriter(file, true)
            } // Open in append mode
            printer = CSVFormat.DEFAULT.print(writer)
            for (coverage in data) {

                // Append the new row of coverage data
                printer.printRecord(
                    toReadableString(coverage.startTime),
                    toReadableString(coverage.endTime),
                    coverage.absCoverage,
                    coverage.timeCoverage,
                    coverage.dataStreamId
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
        return filepath
    }

    private fun toReadableString(instant: Instant): String {
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return localDateTime.toString()

    }
}




