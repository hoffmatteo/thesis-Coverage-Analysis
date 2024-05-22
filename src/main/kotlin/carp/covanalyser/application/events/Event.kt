package carp.covanalyser.application.events

import dk.cachet.carp.common.application.UUID
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

abstract class Event(val id: UUID = UUID.randomUUID()) {

    val createdAt: Instant = Clock.System.now()

    override fun toString(): String {
        return "Event(id=$id, createdAt=$createdAt)"
    }


}