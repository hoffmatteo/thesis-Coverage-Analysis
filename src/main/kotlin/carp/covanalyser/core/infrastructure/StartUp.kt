package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.CoverageCalculator
import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.core.application.events.Event
import carp.covanalyser.core.domain.CoverageAnalysis
import carp.covanalyser.core.domain.CoverageVisitor
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

class StartUp {

    fun startUp() {

        val eventBus = DefaultEventBus()
        var coverageVisitor = CoverageVisitor()
        var coverageCalculator = CoverageCalculator(coverageVisitor)
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus, coverageCalculator)

        // test
        val expectation = AltitudeExpectation(1, "test", 30)
        val dataSource = CAWSDataStore(UUID.randomUUID())
        val exportTarget = CSVExportTarget("test.csv")

        val coverageAnalysis = CoverageAnalysis(
            expectation,
            3600,
            dataSource,
            exportTarget,
            Instant.parse("2020-06-29T14:44:01.251Z"),
            Instant.parse("2020-06-29T16:44:01.251Z")
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))
        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { this.handleCoverageAnalysisCompletedEvent(it) }

        while (true) {
            // do nothing

        }
    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent)
    }
}