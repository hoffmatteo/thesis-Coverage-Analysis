package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

interface DataStore {
    var deploymentId: UUID

    suspend fun obtainData(startTime: Instant, endTime: Instant): List<Measurement<Data>>

}