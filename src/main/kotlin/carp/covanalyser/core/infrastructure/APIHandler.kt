package carp.covanalyser.core.infrastructure

import dk.cachet.carp.common.application.UUID

class APIHandler {


    fun handleRequest(path: String) {
        when (path) {
            "/coverage-analysis" -> {
                val expectationFactory = DefaultExpectationFactory()
                val expectation = expectationFactory.createExpectation("AltitudeExpectation", 10, "test", 10)


                val dataSource = DefaultDataSourceFactory().createDataSource("CAWSDataSource", UUID.randomUUID())
                val exportTarget = DefaultExportTargetFactory().createExportTarget("CSVExportTarget")
                // send event to register coverage analysis?
            }
        }
    }
}