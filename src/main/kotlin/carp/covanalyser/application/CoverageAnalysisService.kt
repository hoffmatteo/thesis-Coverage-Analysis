package carp.covanalyser.application

import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID

interface CoverageAnalysisService {

    suspend fun registerAnalysis(analysis: CoverageAnalysis)

    suspend fun startAnalysis(id: UUID)

    suspend fun stopAnalysis(id: UUID)

    suspend fun stopAllAnalyses()

}