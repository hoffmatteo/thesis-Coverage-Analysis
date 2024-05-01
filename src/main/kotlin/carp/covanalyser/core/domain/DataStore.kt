package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

interface DataStore {
    suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId,
    ): List<Measurement<Data>>

}