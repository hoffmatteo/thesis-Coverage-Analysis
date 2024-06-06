package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID

/**
 * Surrounds a specific [Coverage] value with metadata.
 *
 * @param coverage The coverage value.
 * @param deploymentIds The deployment IDs the coverage was calculated over.
 * @param description A description of the coverage, determined by the expectation.
 * @param subCoverage A list of [CoverageWithMetadata] objects, representing sub-coverages that were used to calculate the current coverage.
 */
data class CoverageWithMetadata(
    val coverage: Coverage,
    val deploymentIds: List<UUID>,
    val description: String,
    val subCoverage: List<CoverageWithMetadata> = emptyList()
)