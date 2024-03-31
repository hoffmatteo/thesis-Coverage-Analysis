package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.IDataSource
import dk.cachet.carp.common.application.UUID

class CarpWS(override var deploymentId: UUID) : IDataSource {
    override fun obtainData(): Any {
        //call endpoint with deploymentId
        //return data
        return 0
    }


}