package carp.covanalyser.application

import carp.covanalyser.domain.CoverageAnalysis

interface CoverageAnalysisService {

    fun registerAnalysis(id: String, analysis: CoverageAnalysis)

    fun startAnalysis(id: String)

    fun stopAnalysis(id: String)

    fun stopAllAnalyses()

}