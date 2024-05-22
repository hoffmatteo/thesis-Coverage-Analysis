package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID

/**
 * Repository to manage [CoverageAnalysis] objects.
 */
interface CoverageAnalysisRepository {

    /**
     * Adds a [CoverageAnalysis] to the repository.
     */
    suspend fun add(coverageAnalysis: CoverageAnalysis)

    /**
     * Deletes a [CoverageAnalysis] from the repository.
     */
    suspend fun delete(id: UUID)

    /**
     * Returns a [CoverageAnalysis] by its [id] if it exists, otherwise returns null.
     *
     * @param id The unique identifier of the coverage analysis.
     */
    suspend fun getBy(id: UUID): CoverageAnalysis?

}