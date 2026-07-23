package com.jcv.mocktests.data

// Represents a course/exam category from exams/testList
data class Course(
    val sheetId: String = "", 
    val title: String = "",
    val fee: String = "0"
)

// Represents a single multiple-choice question
data class Question(
    val text: String = "",
    val options: List<String> = emptyList(),
    val correct: Int = 0
)

// Represents a section (e.g., "Mathematics", "Physics") containing questions
data class Section(
    val name: String,
    val questions: List<Question>
)
