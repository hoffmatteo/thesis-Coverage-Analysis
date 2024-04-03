package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataSource
import carp.covanalyser.core.domain.DataSourceFactory
import dk.cachet.carp.common.application.UUID

class DefaultDataSourceFactory : DataSourceFactory {
    override fun createDataSource(type: String, deploymentId: UUID): DataSource {
        return when (type) {
            "CAWSDataSource" -> CAWSDataSource(deploymentId)
            else -> throw IllegalArgumentException("Unknown data source type: $type")
        }
    }
}