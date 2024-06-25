package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis stop is requested.
 */
class CoverageAnalysisStopRequestedEvent(val coverageAnalysisId: UUID, id: UUID) : Event(id)