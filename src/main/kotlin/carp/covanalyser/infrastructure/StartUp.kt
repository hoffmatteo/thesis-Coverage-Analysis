package carp.covanalyser.infrastructure

import carp.covanalyser.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.application.events.Event
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.data.application.DataStreamId
import kotlinx.datetime.Instant

class StartUp {

    suspend fun startUp() {
        var test = SqliteDBDataStore()
        test.obtainData(
            Instant.fromEpochMilliseconds(1702908535926457 / 1000),
            Instant.fromEpochMilliseconds(1702967584950834 / 1000),
            DataStreamId(
                UUID.parse("a030c0a7-279a-4054-bfce-197cca7a7f94"),
                "Primary Phone",
                DataType("dk.cachet.carp", "stepcount")
            )
        )


        /*

        val eventBus = DefaultEventBus()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        // test
        val dataSource = JSONDataStore()
        val exportTarget = CSVExportTarget("test.csv")

        val locationExpectation = LocationExpectation(1, "Location Service", 4.toDuration(DurationUnit.SECONDS))
        //val coverageAggregator = AverageCoverageAggregator()
        val coverage = locationExpectation.calculateCoverage(
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

         */
    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent)
    }
}