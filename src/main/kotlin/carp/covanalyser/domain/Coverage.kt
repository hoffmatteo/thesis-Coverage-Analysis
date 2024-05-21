package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

/**
 * Represents this project's definition of coverage. Absolute coverage is the total number of available data points divided by the expected number of data points.
 * The time coverage wis the coverage on a time scale, it splits the data points into timeboxes based on the expectation, and compares the number of fulfilled timeboxes with the total number of timeboxes.
 */
//TODO coverage should contain full provenance of where it came from
// DataTypeExpectation: DeploymentId, DataType, DeviceName?, CoverageAnalysisID?
// AggregateExpectation: DeploymentId(s), DataType(s), DeviceName(s), CoverageAnalysisID
// should an expectation define this itself?
// how to model for extensibility?
data class Coverage(
    val absCoverage: Double,
    val timeCoverage: Double,
    val startTime: Instant,
    val endTime: Instant,
)

data class CoverageWithMetadata(
    val coverage: Coverage,
    val deploymentIds: List<UUID>, // always makes sense, since any expectation can be calculated over one or multiple deployments
    val description: String, // DataTypeExpectation: DataType + DeviceName

    // DataTypeExpectation: coverage for Location; ParticipantGroupExpectation: coverage for Location, HeartRate, StepCount,
    // OR deploymentIds good, identifier: String? (can be data types, device names, etc. --> depends on the expectation
    // coverageAnalysisId is then sent as a parameter to the exportTarget

    // full provenance?
)