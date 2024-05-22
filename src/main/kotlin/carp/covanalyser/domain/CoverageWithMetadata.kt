package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID

/**
 * Surrounds a specific [Coverage] value with metadata.
 *
 * @param coverage The coverage value.
 * @param deploymentIds The deployment IDs the coverage was calculated over.
 * @param description A description of the coverage, determined by the expectation.
 */
data class CoverageWithMetadata(
    val coverage: Coverage,
    val deploymentIds: List<UUID>, // always makes sense, since any expectation can be calculated over one or multiple deployments
    val description: String, // DataTypeExpectation: DataType + DeviceName
)