package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

class CoverageAnalysisStopRequestedEvent(val coverageAnalysisId: UUID, id: UUID) : Event(id)