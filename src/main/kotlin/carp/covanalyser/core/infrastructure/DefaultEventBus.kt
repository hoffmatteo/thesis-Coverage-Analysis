package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.application.events.Event
import carp.covanalyser.core.application.events.EventBus
import java.util.concurrent.ConcurrentHashMap
import kotlin.reflect.KClass

class DefaultEventBus : EventBus {
    private val subscribers: MutableMap<KClass<out Event>, MutableList<(Event) -> Unit>> = ConcurrentHashMap()

    override fun <T : Event> subscribe(eventType: KClass<T>, listener: (Event) -> Unit) {
        subscribers.getOrPut(eventType) { mutableListOf() }.add(listener)
    }

    override fun <T : Event> unsubscribe(eventType: KClass<T>, listener: (Event) -> Unit) {
        subscribers[eventType]?.remove(listener)
    }

    override fun <T : Event> publish(event: T) {
        subscribers[event::class]?.forEach { it(event) }
    }

}