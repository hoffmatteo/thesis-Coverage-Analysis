package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.Coverage
import carp.covanalyser.core.domain.ExportTarget

//TODO problem if I want to have extra parameters in constructor
class CSVExportTarget : ExportTarget {
    override suspend fun exportCoverage(data: Coverage): String {
        TODO("Not yet implemented")
    }

}