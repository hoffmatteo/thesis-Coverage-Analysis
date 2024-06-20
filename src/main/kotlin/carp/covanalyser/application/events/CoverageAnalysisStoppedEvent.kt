package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID

class CoverageAnalysisStoppedEvent(val coverageAnalysisId: UUID, id: UUID) : Event(id)