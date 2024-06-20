package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

class CoverageCalculationFailedEvent(id: UUID, val exception: Exception) : Event(id)