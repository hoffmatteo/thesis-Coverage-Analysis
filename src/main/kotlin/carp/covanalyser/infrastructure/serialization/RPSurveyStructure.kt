package carp.covanalyser.infrastructure.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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