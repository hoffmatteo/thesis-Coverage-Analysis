package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID

data class CoverageWithMetadata(
    val coverage: Coverage,
    val deploymentIds: List<UUID>, // always makes sense, since any expectation can be calculated over one or multiple deployments
    val description: String, // DataTypeExpectation: DataType + DeviceName

    // DataTypeExpectation: coverage for Location; ParticipantGroupExpectation: coverage for Location, HeartRate, StepCount,
    // OR deploymentIds good, identifier: String? (can be data types, device names, etc. --> depends on the expectation
    // coverageAnalysisId is then sent as a parameter to the exportTarget

    // full provenance?
)