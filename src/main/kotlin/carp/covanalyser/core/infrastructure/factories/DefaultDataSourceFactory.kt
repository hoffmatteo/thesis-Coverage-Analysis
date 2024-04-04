package carp.covanalyser.core.infrastructure.factories

import carp.covanalyser.core.domain.DataStore
import carp.covanalyser.core.domain.factories.DataSourceFactory
import carp.covanalyser.core.infrastructure.CAWSDataStore
import dk.cachet.carp.common.application.UUID

class DefaultDataSourceFactory : DataSourceFactory {
    override fun createDataSource(type: String, deploymentId: UUID): DataStore {
        return when (type) {
            "CAWSDataSource" -> CAWSDataStore(deploymentId)
            else -> throw IllegalArgumentException("Unknown data source type: $type")
        }
    }
}