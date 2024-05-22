package carp.covanalyser.application.events

import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID

class CoverageAnalysisRequestedEvent(val coverageAnalysis: CoverageAnalysis, uuid: UUID) : Event(uuid)