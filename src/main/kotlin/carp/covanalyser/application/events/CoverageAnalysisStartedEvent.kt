package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage analysis is started.
 * @param id The unique identifier of the event.
 */
class CoverageAnalysisStartedEvent(id: UUID) : Event(id)