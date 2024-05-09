package carp.covanalyser.application.events

import kotlin.reflect.KClass

/**
 * The [EventBus] is used to publish and subscribe to events for communication between modules.
 */
interface EventBus {

    fun <T : Event> subscribe(eventType: KClass<T>, listener: (Event) -> Unit)

    fun <T : Event> unsubscribe(eventType: KClass<T>, listener: (Event) -> Unit)

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
