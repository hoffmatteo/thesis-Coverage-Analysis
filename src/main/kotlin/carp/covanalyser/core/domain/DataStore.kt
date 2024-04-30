package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant

//TODO rethink data store and use it in a more holistic way?
// e.g. AggregateExpectation: aggregate all participant groups with activity last week
interface DataStore {
    var deploymentId: UUID

    //TODO probably needs dataStreamId or something
    suspend fun obtainData(startTime: Instant, endTime: Instant): List<Measurement<Data>>

}