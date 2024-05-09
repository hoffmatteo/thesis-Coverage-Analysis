package carp.covanalyser.application.events

import carp.covanalyser.domain.CoverageAnalysis

class CoverageAnalysisRequestedEvent(val coverageAnalysis: CoverageAnalysis) : Event()