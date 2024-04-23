package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.ExportTarget
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import java.io.File
import java.io.FileWriter
import java.io.IOException

class CSVExportTarget(val filepath: String) : ExportTarget {
    init {
        val file = File(filepath)
        if (!file.exists()) {
            file.createNewFile()
            val writer = FileWriter(file)
            val printer = CSVFormat.DEFAULT.print(writer)
            printer.printRecord("Start Time", "End Time", "Absolute Coverage", "Time Coverage")
            printer.close()
        }
    }

    override suspend fun exportCoverage(data: Coverage): String {
        println("Exporting coverage data to CSV file")
        val file = File(filepath)
        var printer: CSVPrinter? = null

        try {
            val writer = withContext(Dispatchers.IO) {
                FileWriter(file, true)
            } // Open in append mode
            printer = CSVFormat.DEFAULT.print(writer)

            // Append the new row of coverage data
            printer.printRecord(
                data.startTime,
                data.endTime,
                data.absCoverage,
                data.timeCoverage
            )
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
}




