package carp.covanalyser.domain

import dk.cachet.carp.common.application.UUID

interface CoverageAnalysisRepository {

    suspend fun add(coverageAnalysis: CoverageAnalysis)

    suspend fun delete(id: UUID)

    suspend fun getBy(id: UUID): CoverageAnalysis?

}