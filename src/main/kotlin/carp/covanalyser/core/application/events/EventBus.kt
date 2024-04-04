package carp.covanalyser.core.application.events

/**
 * The [EventBus] is used to publish and subscribe to events for communication between modules.
 */
interface EventBus {
    val subscribers: Map<Class<out Event>, MutableList<(Event) -> Unit>>

    fun <T : Event> subscribe(eventType: Class<T>, listener: (T) -> Unit)

    fun <T : Event> unsubscribe(eventType: Class<T>, listener: (T) -> Unit)


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
