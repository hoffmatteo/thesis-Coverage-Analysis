package carp.covanalyser.infrastructure

import carp.covanalyser.application.CoverageAnalysisService
import carp.covanalyser.application.DefaultCoverageAnalysisService
import carp.covanalyser.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.application.events.Event
import carp.covanalyser.application.events.EventBus
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.infrastructure.aggregation.AverageCoverageAggregator
import carp.covanalyser.infrastructure.aggregation.DataTypeAggregation
import carp.covanalyser.infrastructure.aggregation.DeviceAggregation
import carp.covanalyser.infrastructure.aggregation.MultiCoverageExpectation
import carp.covanalyser.infrastructure.expectations.GenericDataTypeExpectation
import carp.covanalyser.infrastructure.expectations.LocationExpectation
import carp.covanalyser.infrastructure.expectations.StepCountExpectation
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.DataType
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class StartUp {
    private var eventBus: EventBus = DefaultEventBus()
    private val inMemoryCoverageAnalysisRepository = InMemoryCoverageAnalysisRepository()
    private var coverageAnalysisService: CoverageAnalysisService =
        DefaultCoverageAnalysisService(eventBus, inMemoryCoverageAnalysisRepository)


    suspend fun startUp() {
        testJsonData()
        //testDBData()

        eventBus.subscribe(CoverageAnalysisCompletedEvent::class) { this.handleCoverageAnalysisCompletedEvent(it) }

        while (true) {
            // do nothing
        }
    }

    private suspend fun testDBData() {
        val dataStore = SQLiteDBDataStore("carp-data.db", "measurements")
        val exportTarget = CSVExportTarget("test_db.csv")

        val measures = mapOf(
            "heartbeat" to 60,
            // "coverage" to 12,
            // "wifi" to 6,
            "ambientlight" to 12,
            "noise" to 12,
            "bluetooth" to 6,
            "freememory" to 60,
            // "app_usage" to 12,
            "connectivity" to 12,
            "weather" to 2,
            "airquality" to 2,
            // "batterystate" to 60,
            "screenevent" to 6,
            "stepcount" to 60,
            "activity" to 60,
            "location" to 60,
            // "mobility" to 60,
            //"polar.hr" to 3600,
            // "polar.ppg" to 3600,
            "deviceinformation" to 1,
            "error" to 6

        )

        val expectations = measures.map { (measure, frequency) ->
            GenericDataTypeExpectation(
                frequency,
                DataType("dk.cachet.carp", measure),
                "Primary Phone",
                1.toDuration(DurationUnit.HOURS)
            ) { true }
        }.toMutableList()

        expectations.add(
            GenericDataTypeExpectation(
                3600, DataType("dk.cachet.carp.polar", "hr"), "Primary Phone",
                1.toDuration(DurationUnit.HOURS)
            ) { true }
        )

        expectations.add(
            GenericDataTypeExpectation(
                3600, DataType("dk.cachet.carp.polar", "ppg"), "Primary Phone",
                1.toDuration(DurationUnit.HOURS)
            ) { true }
        )

        val multiCoverageExpectation = MultiCoverageExpectation()
        multiCoverageExpectation.expectations.addAll(expectations)

        var coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            multiCoverageExpectation,
            1.toDuration(DurationUnit.HOURS),
            listOf(UUID.parse("a030c0a7-279a-4054-bfce-197cca7a7f94")),
            exportTarget,
            dataStore,
            Instant.fromEpochMilliseconds(1702911600000),
            Instant.fromEpochMilliseconds(1702990800000),
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))
    }

    private suspend fun testJsonData() {
        val dataSource = MultiJSONDataStore()
        var exportTarget = CSVExportTarget("test_deployments.csv")

        val locationExpectation = LocationExpectation(1, "Location Service", 1.toDuration(DurationUnit.HOURS))
        val polarExpectation =
            GenericDataTypeExpectation(
                1,
                DataType("dk.cachet.carp.polar", "hr"),
                "Primary Phone",
                1.toDuration(DurationUnit.MINUTES)
            ) { true }
        val stepCountExpectation = StepCountExpectation(1, "Primary Phone", 4.toDuration(DurationUnit.HOURS))

        val dataTypeAggregation = DataTypeAggregation(AverageCoverageAggregator())
        dataTypeAggregation.expectations.add(locationExpectation)

        var coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            dataTypeAggregation,
            2.toDuration(DurationUnit.DAYS),
            listOf(
                UUID.parse("7a46a288-7f8e-4242-bedf-ebc80bc9e693"),
                UUID.parse("301d0cb4-0482-411d-b58e-f347586dae84"),
                UUID.parse("09824317-e14c-4b48-9ddf-122d7aadaa87"),
                UUID.parse("521dd33b-cd9a-4991-b0d3-04c781c0704c"),
                UUID.parse("714468bc-97e6-4781-88e9-20a426f8c8ad"),
                UUID.parse("4f5d07ef-2ce8-4b10-9cd7-15f34c90c06a"),
                UUID.parse("4cdec469-5b5b-495f-8a3c-242c877b190d"),
                UUID.parse("f63bc945-7113-4974-a4ea-81a197d30697"),
                UUID.parse("04ddc67c-4e2d-4468-919b-9ca06e3451db"),
                UUID.parse("3ffb09b9-271a-418e-b15d-0975ebec943d")
            ),
            exportTarget,
            dataSource,
            Instant.fromEpochMilliseconds(1704067200000),
            Instant.fromEpochMilliseconds(1704239999000),
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))


        exportTarget = CSVExportTarget("test_devices.csv")


        val deviceAggregation = DeviceAggregation("Primary Phone", AverageCoverageAggregator())
        deviceAggregation.expectations.add(polarExpectation)
        deviceAggregation.expectations.add(stepCountExpectation)

        coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            deviceAggregation,
            2.toDuration(DurationUnit.DAYS),
            listOf(
                UUID.parse("7a46a288-7f8e-4242-bedf-ebc80bc9e693"),
                UUID.parse("301d0cb4-0482-411d-b58e-f347586dae84"),
                UUID.parse("09824317-e14c-4b48-9ddf-122d7aadaa87"),
                UUID.parse("521dd33b-cd9a-4991-b0d3-04c781c0704c"),
                UUID.parse("714468bc-97e6-4781-88e9-20a426f8c8ad"),
                UUID.parse("4f5d07ef-2ce8-4b10-9cd7-15f34c90c06a"),
                UUID.parse("4cdec469-5b5b-495f-8a3c-242c877b190d"),
                UUID.parse("f63bc945-7113-4974-a4ea-81a197d30697"),
                UUID.parse("04ddc67c-4e2d-4468-919b-9ca06e3451db"),
                UUID.parse("3ffb09b9-271a-418e-b15d-0975ebec943d")
            ),
            exportTarget,
            dataSource,
            Instant.fromEpochMilliseconds(1704067200000),
            Instant.fromEpochMilliseconds(1704239999000),
        )



        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis))


    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent)
    }
}