package carp.covanalyser.infrastructure.aggregation

import carp.covanalyser.domain.CoverageAggregator
import carp.covanalyser.domain.DataStreamExpectation

class AggregationFactory {

    fun createDeviceAggregations(
        dataStreamExpectations: List<DataStreamExpectation>, coverageAggregator: CoverageAggregator
    ): List<DeviceAggregation> {
        val deviceAggregations = mutableListOf<DeviceAggregation>()

        // Group DataStreamExpectations by deviceName
        val groupedExpectations = dataStreamExpectations.groupBy { it.deviceName }

        // Create a DeviceAggregation for each group
        for ((deviceName, expectations) in groupedExpectations) {
            val deviceAggregation = DeviceAggregation(deviceName, coverageAggregator)
            expectations.forEach { deviceAggregation.expectations.add(it) }
            deviceAggregations.add(deviceAggregation)
        }
        return deviceAggregations
    }


}