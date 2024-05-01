package carp.covanalyser.core.infrastructure

import carp.covanalyser.core.domain.DataStreamExpectation

class AggregationFactory {

    fun createDeviceAggregations(
        dataStreamExpectations: List<DataStreamExpectation>
    ): List<DeviceAggregation> {
        val deviceAggregations = mutableListOf<DeviceAggregation>()

        // Group DataStreamExpectations by deviceName
        val groupedExpectations = dataStreamExpectations.groupBy { it.deviceName }

        // Create a DeviceAggregation for each group
        for ((deviceName, expectations) in groupedExpectations) {
            val deviceAggregation = DeviceAggregation(deviceName)
            expectations.forEach { deviceAggregation.expectations.add(it) }
            deviceAggregations.add(deviceAggregation)
        }

        return deviceAggregations
    }


}