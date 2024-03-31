package carp.covanalyser.core.domain

import dk.cachet.carp.common.application.UUID

interface IDataSource {
    var deploymentId: UUID

    fun obtainData(): Any
}