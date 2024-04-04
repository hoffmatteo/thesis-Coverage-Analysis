package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.application.events.EventBus
import carp.covanalyser.core.domain.CoverageAnalysis
import carp.covanalyser.core.infrastructure.factories.DefaultDataSourceFactory
import carp.covanalyser.core.infrastructure.factories.DefaultExpectationFactory
import carp.covanalyser.core.infrastructure.factories.DefaultExportTargetFactory
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class APIHandler(val eventBus: EventBus) {
    private val defaultExpectationFactory = DefaultExpectationFactory()
    private val defaultDataSourceFactory = DefaultDataSourceFactory()
    private val defaultExportTargetFactory = DefaultExportTargetFactory()

    //TODO not sure if I need or should have factory pattern for this
    fun handleRequest(path: String) {
        when (path) {
            "/coverage-analysis" -> {
                val expectation = defaultExpectationFactory.createExpectation("AltitudeExpectation", 10, "test", 10)
                val dataSource = defaultDataSourceFactory.createDataSource("CAWSDataSource", UUID.randomUUID())
                val exportTarget = defaultExportTargetFactory.createExportTarget("CSVExportTarget")

                val coverageAnalysis = CoverageAnalysis(
                    expectation,
                    3600,
                    dataSource,
                    exportTarget,
                    Instant.parse("2020-06-30T14:44:01.251Z"),
                    Instant.parse("2020-06-31T14:44:01.251Z")
                )
                eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))
            }
        }
    }
}