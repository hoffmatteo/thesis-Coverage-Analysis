package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.DataTypeExpectation

class AggregationFactory {

    fun createDeviceAggregations(
        dataTypeExpectations: List<DataTypeExpectation>, coverageAggregator: CoverageAggregator
    ): List<DeviceAggregation> {
        val deviceAggregations = mutableListOf<DeviceAggregation>()

        // Group DataStreamExpectations by deviceName
        val groupedExpectations = dataTypeExpectations.groupBy { it.deviceName }

        // Create a DeviceAggregation for each group
        for ((deviceName, expectations) in groupedExpectations) {
            val deviceAggregation = DeviceAggregation(deviceName, coverageAggregator)
            expectations.forEach { deviceAggregation.expectations.add(it) }
            deviceAggregations.add(deviceAggregation)
        }
        return deviceAggregations
    }


}