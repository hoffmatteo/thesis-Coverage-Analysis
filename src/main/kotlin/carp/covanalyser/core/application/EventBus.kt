package carp.covanalyser.core.application

/**
 * The [EventBus] is used to publish and subscribe to events for communication between modules.
 */
class EventBus {

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
}