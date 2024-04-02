package carp.covanalyser.core.domain

class CoverageAnalysis(
    var expectation: IExpectation,
    var timeFrameSeconds: Int,
    var dataSource: IDataSource,
    var exportTarget: IExportTarget
) {
    suspend fun calculateCoverage(): Boolean {
        var data = dataSource.obtainData()


        //calc coverage with data and expectation
        val coverage = Coverage(0.0, 0.0, mutableListOf<Int>())

        exportTarget.exportCoverage(coverage)

        return true
    }


}