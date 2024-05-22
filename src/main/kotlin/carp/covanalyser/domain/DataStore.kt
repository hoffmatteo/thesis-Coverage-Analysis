package carp.covanalyser.domain

import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.DataStreamId
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

/**
 * Represents a data store to obtain data from.
 */
interface DataStore {
    /**
     * Obtains [Measurement]s from the data store.
     *
     * @param startTime The start time of the measurements to obtain.
     * @param endTime The end time of the measurements to obtain.
     * @param dataStreamId The [DataStreamId] of the measurements to obtain.
     */
    suspend fun obtainData(
        startTime: Instant,
        endTime: Instant,
        dataStreamId: DataStreamId,
    ): List<Measurement<Data>>

}