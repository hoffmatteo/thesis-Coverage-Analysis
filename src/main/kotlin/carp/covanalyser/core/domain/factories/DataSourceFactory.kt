package carp.covanalyser.core.domain.factories

import carp.covanalyser.core.domain.DataStore
import dk.cachet.carp.common.application.UUID

interface DataSourceFactory {
    fun createDataSource(type: String, deploymentId: UUID): DataStore
}