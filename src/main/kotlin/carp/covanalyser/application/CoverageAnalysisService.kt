package carp.covanalyser.application

import carp.covanalyser.application.events.EventBus
import carp.covanalyser.domain.CoverageAnalysis
import dk.cachet.carp.common.application.UUID

/**
 * Service to manage [CoverageAnalysis] objects.
 */
interface CoverageAnalysisService {
    val eventBus: EventBus

    /**
     * Registers a new [CoverageAnalysis].
     * @param analysis The [CoverageAnalysis] to register.
     */
    suspend fun registerAnalysis(analysis: CoverageAnalysis)

    /**
     * Starts the [CoverageAnalysis] with the given [id].
     * @param id The unique identifier of the [CoverageAnalysis] to start.
     */
    suspend fun startAnalysis(id: UUID)

    /**
     * Stops the [CoverageAnalysis] with the given [id].
     * @param id The unique identifier of the [CoverageAnalysis] to stop.
     */
    suspend fun stopAnalysis(id: UUID)

    /**
     * Stops all running analyses.
     */
    suspend fun stopAllAnalyses()

}