package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

class CoverageAnalysisStopFailedEvent(val coverageAnalysisId: UUID, id: UUID, val exception: Exception) : Event(id)