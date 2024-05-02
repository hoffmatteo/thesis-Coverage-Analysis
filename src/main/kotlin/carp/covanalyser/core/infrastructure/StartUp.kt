package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.Event
import carp.covanalyser.core.infrastructure.expectations.LocationExpectation
import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StartUp {

    suspend fun startUp() {

        val eventBus = DefaultEventBus()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        // test
        val dataSource = CAWSDataStore()
        val exportTarget = CSVExportTarget("test.csv")

        // deploymentID d396f162-4739-4099-a4c8-a2ceca74876d
        // data type dk.cachet.carp.location
        // device role name Location Service

        val locationExpectation = LocationExpectation(2, "Location Service", 30.toDuration(DurationUnit.SECONDS))
        //val coverageAggregator = AverageCoverageAggregator()
        var coverage = locationExpectation.calculateCoverage(
            Instant.fromEpochMilliseconds(1710674244963680L / 1000),
            Instant.fromEpochMilliseconds(1710674299451360L / 1000),
            listOf(UUID.parse("7e33ff2c-47f4-4545-92fa-286b4e3719fe")),
            dataSource
        ).first()
        println("Coverage Result: " + coverage)

        /*
        val locationExpectation = LocationExpectation(1, "phone", 30)
        val stepCountExpectation = StepCountExpectation(1, "phone", 30)

        val coverageAggregator = AverageCoverageAggregator()


        val deviceAggregations =
            AggregationFactory().createDeviceAggregations(
                listOf(locationExpectation, stepCountExpectation),
                coverageAggregator
            )

        val protocolAggregation = ProtocolAggregation(coverageAggregator)
        protocolAggregation.expectations.addAll(deviceAggregations)

        val studyAggregation = StudyAggregation(coverageAggregator)
        studyAggregation.expectations.add(protocolAggregation)

        val coverageAnalysis = CoverageAnalysis(
            studyAggregation,
            3600,
            listOf(UUID.randomUUID()),
            exportTarget,
            dataSource,
            Instant.parse("2020-06-29T14:44:01.251Z"),
            Instant.parse("2020-06-29T16:44:01.251Z")
        )



        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))

         */
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