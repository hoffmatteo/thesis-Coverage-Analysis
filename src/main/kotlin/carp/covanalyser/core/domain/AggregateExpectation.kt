package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

abstract class AggregateExpectation : Expectation {
    open var expectations: MutableList<Expectation> = emptyList<Expectation>().toMutableList()

    //TODO strategy design pattern for aggregation?
    // this is not really an expectation....
    // place where
    override suspend fun calculateCoverage(
        startTime: Instant,
        endTime: Instant,
        deploymentIDs: List<UUID>,
        dataStore: DataStore
    ): Coverage {
        for (expectation in expectations) {
            expectation.calculateCoverage(startTime, endTime, deploymentIDs, dataStore)
        }
        return Coverage(0.0, 0.0, startTime, endTime)
        // average out expectations --> problem here is what I pass?
    }

    // TODO come up with examples for this --> user is active at least once a day
    // first simple drill-down hierarchy --> study, participantgroup, device, data stream?
    // then more complex aggregations --> all users that have been active the last week
    // expectations from study protocol

    //TODO
    // fun calculateCoverage() -> expectations.calculateCoverage average it out
}
