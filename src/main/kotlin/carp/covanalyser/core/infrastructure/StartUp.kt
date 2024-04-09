package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.application.events.Event
import carp.covanalyser.core.domain.CoverageAnalysis
import carp.covanalyser.core.infrastructure.factories.DefaultDataSourceFactory
import carp.covanalyser.core.infrastructure.factories.DefaultExpectationFactory
import carp.covanalyser.core.infrastructure.factories.DefaultExportTargetFactory
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class StartUp {

    suspend fun startUp() {
        val defaultExpectationFactory = DefaultExpectationFactory()
        val defaultDataSourceFactory = DefaultDataSourceFactory()
        val defaultExportTargetFactory = DefaultExportTargetFactory()

        val eventBus = DefaultEventBus()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        // test
        val expectation = defaultExpectationFactory.createExpectation("AltitudeExpectation", 2, "test", 36000)
        val dataSource = defaultDataSourceFactory.createDataSource("CAWSDataSource", UUID.randomUUID())
        val exportTarget = defaultExportTargetFactory.createExportTarget("CSVExportTarget")

        val coverageAnalysis = CoverageAnalysis(
            expectation,
            3600,
            dataSource,
            exportTarget,
            Instant.parse("2020-06-29T14:44:01.251Z"),
            Instant.parse("2020-06-30T14:44:01.251Z")
        )

        coverageAnalysis.calculateCoverage(
            Instant.parse("2020-06-29T14:44:01.251Z"),
            Instant.parse("2020-06-30T14:44:01.251Z")
        )
        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))
        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { this.handleCoverageAnalysisCompletedEvent(it) }
    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent.test)
    }
}