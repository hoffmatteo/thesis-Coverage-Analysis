package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Instant

abstract class AggregateExpectation : Expectation {
    open var expectations: MutableList<Expectation> = emptyList<Expectation>().toMutableList()

    override suspend fun calculateCoverage(startTime: Instant, endTime: Instant, deploymentIDs: List<UUID>): Coverage {
        TODO("Not yet implemented")
        // average out expectations --> problem here is what I pass?
    }

    // TODO come up with examples for this --> user is active at least once a day
    // first simple drill-down hierarchy --> study, participantgroup, device, data stream?
    // then more complex aggregations --> all users that have been active the last week
    // expectations from study protocol

    //TODO
    // fun calculateCoverage() -> expectations.calculateCoverage average it out
}
