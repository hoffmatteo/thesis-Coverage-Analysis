package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.DefaultCoverageAnalysisService
import carp.covanalyser.core.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.core.application.events.Event

class StartUp {

    fun startUp() {

        val eventBus = DefaultEventBus()
        val coverageAnalysisService = DefaultCoverageAnalysisService(eventBus)

        // test
        val dataSource = CAWSDataStore()
        val exportTarget = CSVExportTarget("test.csv")
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