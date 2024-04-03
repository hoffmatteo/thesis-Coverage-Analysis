package carp.covanalyser.core.domain

class CoverageAnalysis(
    var expectation: Expectation,
    var timeFrameSeconds: Int,
    var dataSource: DataSource,
    var exportTarget: ExportTarget
) {
    suspend fun calculateCoverage(): Boolean {
        var data = dataSource.obtainData()


        //calc coverage with data and expectation
        val coverage = Coverage(0.0, 0.0, mutableListOf<Int>())

        exportTarget.exportCoverage(coverage)

        return true
    }


}