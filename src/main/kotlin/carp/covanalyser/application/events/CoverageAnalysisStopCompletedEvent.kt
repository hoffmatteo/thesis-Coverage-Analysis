package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis is stopped.
 */
class CoverageAnalysisStopCompletedEvent(val coverageAnalysisId: UUID, id: UUID) : Event(id)