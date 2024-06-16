package carp.covanalyser.infrastructure

import carp.covanalyser.application.CoverageAnalysisService
import carp.covanalyser.application.DefaultCoverageAnalysisService
import carp.covanalyser.application.events.CoverageAnalysisCompletedEvent
import carp.covanalyser.application.events.CoverageAnalysisRequestedEvent
import carp.covanalyser.application.events.Event
import carp.covanalyser.application.events.EventBus
import carp.covanalyser.domain.AggregateExpectation
import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.Expectation
import carp.covanalyser.domain.ExportTarget
import carp.covanalyser.infrastructure.aggregation.AverageCoverageAggregator
import carp.covanalyser.infrastructure.aggregation.DeploymentExpectationAggregator
import carp.covanalyser.infrastructure.aggregation.ParticipantGroupAggregation
import carp.covanalyser.infrastructure.expectations.*
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
        testSurveyData()
        //testJsonData()
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


        val coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            expectations,
            1.toDuration(DurationUnit.HOURS),
            listOf(UUID.parse("a030c0a7-279a-4054-bfce-197cca7a7f94")),
            exportTarget,
            dataStore,
            Instant.fromEpochMilliseconds(1702911600000),
            Instant.fromEpochMilliseconds(1702990800000),
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))
    }

    private suspend fun testSurveyData() {
        val dataSource = JSONDataStore("test_data\\survey_test.json")
        val exportTarget: ExportTarget =
            CSVExportTarget("C:\\Users\\matte\\Desktop\\DTU\\thesis_analysis\\surveys_test\\test_survey.csv")

        val who5Expectation = WHO5Expectation(1, 7.toDuration(DurationUnit.DAYS))
        val hadsExpectation = HADSExpectation(1, 14.toDuration(DurationUnit.DAYS))
        val stepCountExpectation = StepCountExpectation(1, "Primary Phone", 24.toDuration(DurationUnit.HOURS))
        val coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            listOf(who5Expectation, hadsExpectation, stepCountExpectation),
            28.toDuration(DurationUnit.DAYS),
            listOf(
                UUID.parse("669fcecb-0071-4404-8b47-0debd2c2e2b5"),
                UUID.parse("daac0d12-f9ef-4444-8154-22e8cc1f2cb6"),
                UUID.parse("0b15f421-5107-4201-98be-1473a85532ea"),
                UUID.parse("052a3bca-358d-4df6-acb7-34b6794e7e44"),
                UUID.parse("fdf7000d-3962-4f37-899f-f9725adf7812"),
                UUID.parse("a9aa0c37-4b92-422d-bffe-b6a292f9ab7f"),
                UUID.parse("e6c61354-537b-4ef6-b430-d3001db8eeaf"),
                UUID.parse("412df3a4-6b3e-4db7-adeb-d218cc2227b1"),
                UUID.parse("64bedad1-e28c-4c53-81a5-5a27b549adda"),
                UUID.parse("8fb3b01f-c641-4580-831f-7959c88531b8")
            ),
            exportTarget,
            dataSource,
            Instant.parse("2023-01-01T15:00:00Z"),
            Instant.parse("2023-07-01T15:00:00Z"),
        )

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))


    }

    private suspend fun testJsonData() {
        val dataSource = JSONDataStore("test_data\\sensor_test.json")
        var exportTarget: ExportTarget =
            CSVExportTarget("C:\\Users\\matte\\Desktop\\DTU\\thesis_analysis\\json_test\\test_datastreams.csv")

        val locationExpectation = LocationExpectation(1, "Location Service", 1.toDuration(DurationUnit.HOURS))
        val polarExpectation =
            GenericDataTypeExpectation(
                1,
                DataType("dk.cachet.carp.polar", "hr"),
                "Primary Phone",
                1.toDuration(DurationUnit.MINUTES)
            ) { true }
        val stepCountExpectation = StepCountExpectation(1, "Primary Phone", 4.toDuration(DurationUnit.HOURS))


        var coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            listOf(locationExpectation, polarExpectation, stepCountExpectation),
            8.toDuration(DurationUnit.HOURS),
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

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))

        exportTarget =
            CSVExportTarget("C:\\Users\\matte\\Desktop\\DTU\\thesis_analysis\\json_test\\test_deployments.csv")
        val deploymentLocation =
            AggregateExpectation<Expectation>(AverageCoverageAggregator(), DeploymentExpectationAggregator())
        deploymentLocation.expectations.add(locationExpectation)
        val deploymentPolar =
            AggregateExpectation<Expectation>(AverageCoverageAggregator(), DeploymentExpectationAggregator())
        deploymentPolar.expectations.add(polarExpectation)
        val deploymentStepCount =
            AggregateExpectation<Expectation>(AverageCoverageAggregator(), DeploymentExpectationAggregator())
        deploymentStepCount.expectations.add(stepCountExpectation)

        coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            listOf(deploymentPolar, deploymentLocation, deploymentStepCount),
            8.toDuration(DurationUnit.HOURS),
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

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))


        /*
        exportTarget = CSVExportTarget("test_devices.csv")


        val deviceAggregation = DeviceAggregation("Primary Phone", AverageCoverageAggregator())
        deviceAggregation.expectations.add(polarExpectation)
        deviceAggregation.expectations.add(stepCountExpectation)

        coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            deviceAggregation,
            12.toDuration(DurationUnit.HOURS),
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

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))
*/
        exportTarget = CSVExportTarget("test_participant_group.csv")


        val participantGroupAggregation = ParticipantGroupAggregation(AverageCoverageAggregator())
        participantGroupAggregation.expectations.add(locationExpectation)
        participantGroupAggregation.expectations.add(polarExpectation)
        participantGroupAggregation.expectations.add(stepCountExpectation)
        /*

        coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            listOf(participantGroupAggregation),
            8.toDuration(DurationUnit.HOURS),
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

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))





        exportTarget =
            CSVExportTarget("C:\\Users\\matte\\Desktop\\DTU\\thesis_analysis\\json_test\\test_study.csv")

        val studyAggregation = StudyAggregation(AverageCoverageAggregator())
        studyAggregation.expectations.add(participantGroupAggregation)

        var coverageAnalysis = CoverageAnalysis(
            UUID.randomUUID(),
            listOf(studyAggregation),
            8.toDuration(DurationUnit.HOURS),
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

        eventBus.publish(CoverageAnalysisRequestedEvent(coverageAnalysis, UUID.randomUUID()))

         */


    }

    private fun handleCoverageAnalysisCompletedEvent(event: Event) {
        val coverageAnalysisCompletedEvent = event as CoverageAnalysisCompletedEvent
        println("Coverage analysis completed " + coverageAnalysisCompletedEvent)
    }
}