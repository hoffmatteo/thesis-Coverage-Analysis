package carp.covanalyser.application.events

import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis is rejected.
 */
class CoverageAnalysisRejectedEvent(val coverageAnalysis: CoverageAnalysis, id: UUID, val exception: Exception) :
    Event(id)