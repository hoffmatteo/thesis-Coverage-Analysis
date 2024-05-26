package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage is calculated.
 * @param id The unique identifier of the event.
 */
class CoverageCalculatedEvent(id: UUID) : Event(id)