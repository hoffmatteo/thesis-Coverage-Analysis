package carp.covanalyser.application.events

import kotlin.reflect.KClass

/**
 * The [EventBus] is used to publish and subscribe to events for communication between modules.
 */
interface EventBus {
    /**
     * Subscribe to events of a specific type by registering a listener.
     * @param eventType The type of event to subscribe to.
     * @param listener The listener to invoke when an event of the specified type is published.
     */
    fun <T : Event> subscribe(eventType: KClass<T>, listener: (Event) -> Unit)

    /**
     * Publish an event to all subscribers.
     * @param event The event to publish.
     */
    fun <T : Event> publish(event: T)
}


/*
Since it is in the application layer, domain layer cannot use it! Only communication for classes in infrastructure
and application layer!
possible events:
- dataRequested
- dataReceived
- coverageAnalysisRequested
- coverageAnalysisCompleted
- coverageAnalysisFailed
- coverageAnalysisExported
- ....

 */
