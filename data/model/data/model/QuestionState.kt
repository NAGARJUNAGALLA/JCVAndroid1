package com.jcv.mocktests.data.model

enum class Status {
    NOT_VISITED,
    NOT_ANSWERED,
    ANSWERED,
    MARKED_FOR_REVIEW,
    ANSWERED_AND_MARKED
}

data class QuestionUserAnswer(
    var status: Status = Status.NOT_VISITED,
    var selectedOption: Int? = null
)

data class Question(
    val id: Int,
    val type: String = "mcq",
    val text: String,
    val options: List<String>,
    val correct: Int
)

data class ExamSection(
    val name: String,
    val questions: List<Question>,
    val globalStartIndex: Int
)
