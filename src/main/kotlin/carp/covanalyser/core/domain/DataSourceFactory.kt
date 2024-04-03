package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID

interface DataSourceFactory {
    fun createDataSource(type: String, deploymentId: UUID): DataSource
}