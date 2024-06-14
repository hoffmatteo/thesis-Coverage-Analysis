package carp.covanalyser.infrastructure

import carp.covanalyser.application.events.Event
import carp.covanalyser.application.events.EventBus
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

/**
 * Default implementation of [EventBus] which uses a simple in-memory map to store subscribers.
 */
class DefaultEventBus : EventBus {
    private val subscribers: MutableMap<KClass<out Event>, MutableList<(Event) -> Unit>> = ConcurrentHashMap()

    override fun <T : Event> subscribe(eventType: KClass<T>, listener: (Event) -> Unit) {
        subscribers.getOrPut(eventType) { mutableListOf() }.add(listener)
    }

    override fun <T : Event> publish(event: T) {
        subscribers[event::class]?.forEach { it(event) }
    }

}