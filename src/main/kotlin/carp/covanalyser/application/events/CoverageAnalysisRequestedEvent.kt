package carp.covanalyser.application.events

import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis is requested.
 * @param coverageAnalysis The [CoverageAnalysis] that is requested.
 * @param uuid The unique identifier of the event.
 */
class CoverageAnalysisRequestedEvent(val coverageAnalysis: CoverageAnalysis, id: UUID) : Event(id)