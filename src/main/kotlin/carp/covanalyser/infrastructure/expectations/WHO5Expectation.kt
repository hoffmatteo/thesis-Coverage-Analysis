package carp.covanalyser.infrastructure.expectations

import carp.covanalyser.domain.DataTypeExpectation
import dk.cachet.carp.common.application.data.Data
import dk.cachet.carp.common.application.data.DataType
import dk.cachet.carp.common.infrastructure.serialization.CustomData
import dk.cachet.carp.data.application.Measurement
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Duration

class WHO5Expectation(numDataPoints: Int, duration: Duration) :
    DataTypeExpectation(
        numDataPoints, DataType("dk.cachet.carp", "who"),
        //TODO maybe change this
        "Survey Service",
        duration
    ) {
    override fun isValid(input: Measurement<Data>): Boolean {
        var customData = input.data as CustomData
        val data = Json.decodeFromString<>()
        return data.jsonSource.surveyResult.results.values.any { it.results.answer > 0 }
    }
}


@Serializable
data class SurveyData(
    @SerialName("__type") val type: String,
    val surveyResult: SurveyResult
)

@Serializable
data class SurveyResult(
    val identifier: String,
    val startDate: String,
    val endDate: String,
    val results: Map<String, StepResult>
)

@Serializable
data class StepResult(
    val identifier: String,
    val startDate: String,
    val endDate: String,
    val questionTitle: String,
    val results: Answer,
    val answerFormat: AnswerFormat
)

@Serializable
data class Answer(
    val answer: Int
)

@Serializable
data class AnswerFormat(
    val minValue: Int,
    val maxValue: Int,
    val suffix: String?,
    val questionType: String
)