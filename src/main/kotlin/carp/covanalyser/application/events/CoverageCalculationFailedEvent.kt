package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

/**
 * Event that is triggered when a coverage calculation failed.
 */
class CoverageCalculationFailedEvent(id: UUID, val exception: Exception) : Event(id)