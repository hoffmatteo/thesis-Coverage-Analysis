package carp.covanalyser.infrastructure

import carp.covanalyser.application.CoverageAnalysisService
import carp.covanalyser.application.DefaultCoverageAnalysisService
import carp.covanalyser.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.application.events.Event
import carp.covanalyser.application.events.EventBus
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.infrastructure.expectations.LocationExpectation
import carp.covanalyser.infrastructure.expectations.StepCountExpectation
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StartUp {
    private var eventBus: EventBus = DefaultEventBus()
    private var coverageAnalysisService: CoverageAnalysisService = DefaultCoverageAnalysisService(eventBus)


    suspend fun startUp() {
        //testJsonData()
        testDBData()

        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { this.handleCoverageAnalysisCompletedEvent(it) }

        while (true) {
            // do nothing
        }
    }

    private suspend fun testDBData() {
        val dataStore = SQLiteDBDataStore("carp-data.db", "measurements")
        val exportTarget = CSVExportTarget("test_db.csv")

        val stepCountExpectation = StepCountExpectation(1, "Primary Phone", 30.toDuration(DurationUnit.MINUTES))

        val coverageAnalysis = CoverageAnalysis(
            stepCountExpectation,
            6.toDuration(DurationUnit.HOURS),
            listOf(UUID.parse("a030c0a7-279a-4054-bfce-197cca7a7f94")),
            exportTarget,
            dataStore,
            Instant.fromEpochMilliseconds(1702908535926457 / 1000),
            Instant.fromEpochMilliseconds(1702967584950834 / 1000),
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))
    }

    private suspend fun testJsonData() {
        val dataSource = JSONDataStore()
        val exportTarget = CSVExportTarget("test.csv")

        val locationExpectation = LocationExpectation(1, "Location Service", 4.toDuration(DurationUnit.SECONDS))

        val coverageAnalysis = CoverageAnalysis(
            locationExpectation,
            60.toDuration(DurationUnit.SECONDS),
            listOf(UUID.parse("7e33ff2c-47f4-4545-92fa-286b4e3719fe")),
            exportTarget,
            dataSource,
            Instant.fromEpochMilliseconds(1710674244963680L / 1000),
            Instant.fromEpochMilliseconds(1710674299451360L / 1000),
        )
        
        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))


    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent)
    }
}