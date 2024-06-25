package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis stop fails.
 */
class CoverageAnalysisStopFailedEvent(val coverageAnalysisId: UUID, id: UUID, val exception: Exception) : Event(id)