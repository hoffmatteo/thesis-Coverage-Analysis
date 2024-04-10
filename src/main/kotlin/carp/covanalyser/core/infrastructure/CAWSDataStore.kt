package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataStore
import createDataPoints
import dk.cachet.carp.common.application.UUID
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.data.application.Measurement
import kotlinx.datetime.Instant
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class CAWSDataStore(override var deploymentId: UUID) : DataStore {
    override suspend fun obtainData(startTime: Instant, endTime: Instant): List<Measurement<Data>> {
        val dataPoints: List<Measurement<Data>> = createDataPoints(
            1000,
            listOf(10, 20, 30),
            listOf(10.toDuration(DurationUnit.SECONDS), 10.toDuration(DurationUnit.SECONDS)),
            Instant.parse("2020-06-29T14:44:01.251Z")
        )

        return dataPoints
    }
}