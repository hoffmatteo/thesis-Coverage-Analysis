package carp.covanalyser.core.application

import carp.covanalyser.core.domain.CoverageAnalysis

interface CoverageAnalysisService {

    fun registerAnalysis(id: String, analysis: CoverageAnalysis)

    fun startRecurringAnalysis(id: String)

    fun startFullAnalysis(id: String)

    fun stopAnalysis(id: String)

    fun stopAllAnalyses()

}