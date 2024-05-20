package carp.covanalyser.infrastructure

import carp.covanalyser.domain.CoverageAnalysis
import carp.covanalyser.domain.CoverageAnalysisRepository
import dk.cachet.carp.common.application.UUID

class InMemoryCoverageAnalysisRepository : CoverageAnalysisRepository {
    private val coverageAnalyses = mutableMapOf<UUID, CoverageAnalysis>()

    override suspend fun add(coverageAnalysis: CoverageAnalysis) {
        coverageAnalyses[coverageAnalysis.id] = coverageAnalysis
    }

    override suspend fun delete(id: UUID) {
        coverageAnalyses.remove(id)
    }

    override suspend fun getBy(id: UUID): CoverageAnalysis? {
        return coverageAnalyses[id]
    }

}