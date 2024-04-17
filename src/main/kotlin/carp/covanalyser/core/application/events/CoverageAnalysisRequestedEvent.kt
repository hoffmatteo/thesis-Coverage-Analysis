package carp.covanalyser.core.application.events

import carp.covanalyser.core.domain.CoverageAnalysis

class CoverageAnalysisRequestedEvent(val coverageAnalysis: CoverageAnalysis) : Event()