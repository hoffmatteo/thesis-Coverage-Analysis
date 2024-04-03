package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement

interface DataSource {
    var deploymentId: UUID

    suspend fun obtainData(): List<Measurement<Data>>
}