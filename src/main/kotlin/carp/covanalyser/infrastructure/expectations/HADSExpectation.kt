package carp.covanalyser.infrastructure.expectations

import carp.covanalyser.domain.DataTypeExpectation
import carp.covanalyser.infrastructure.serialization.SurveyData
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.common.infrastructure.serialization.CustomData
import dk.cachet.carp.data.application.Measurement
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class HADSExpectation(numDataPoints: Int, duration: Duration) :
    DataTypeExpectation(
        numDataPoints, DataType("dk.cachet.carp", "hads"),
        //TODO maybe change this
        "Survey Service",
        duration
    ) {
    override fun isValid(input: Measurement<Data>): Boolean {
        val customData = input.data as CustomData
        val data = Json.decodeFromString<SurveyData>(customData.jsonSource)

        for ((step, result) in data.surveyResult.results) {
            val answer = result.results.answer

            if (answer !in 0..3) {
                return false
            }
        }
        return true
    }
}
