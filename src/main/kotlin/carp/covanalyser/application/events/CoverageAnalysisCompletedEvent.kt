package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis is completed.
 * @param id The unique identifier of the event.
 */
class CoverageAnalysisCompletedEvent(id: UUID) : Event(id)