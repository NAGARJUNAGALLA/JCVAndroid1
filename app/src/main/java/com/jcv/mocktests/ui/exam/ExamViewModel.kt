package com.jcv.mocktests.ui.exam

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jcv.mocktests.data.model.*
import com.jcv.mocktests.data.repository.CbtRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ExamViewModel(private val repository: CbtRepository = CbtRepository()) : ViewModel() {

    private val _sections = MutableStateFlow<List<ExamSection>>(emptyList())
    val sections: StateFlow<List<ExamSection>> = _sections.asStateFlow()

    private val _userAnswers = MutableStateFlow<Map<Pair<Int, Int>, QuestionUserAnswer>>(emptyMap())
    val userAnswers: StateFlow<Map<Pair<Int, Int>, QuestionUserAnswer>> = _userAnswers.asStateFlow()

    val currentSectionIndex = MutableStateFlow(0)
    val currentQuestionIndex = MutableStateFlow(0)
    val tempSelection = MutableStateFlow<Int?>(null)

    private val _timeLeft = MutableStateFlow(0)
    val timeLeft: StateFlow<Int> = _timeLeft.asStateFlow()

    private var timerJob: Job? = null

    fun setupExam(sectionsList: List<ExamSection>, totalMinutes: Int) {
        _sections.value = sectionsList
        val initialMap = mutableMapOf<Pair<Int, Int>, QuestionUserAnswer>()
        sectionsList.forEachIndexed { sIdx, sec ->
            sec.questions.forEachIndexed { qIdx, _ ->
                initialMap[Pair(sIdx, qIdx)] = QuestionUserAnswer()
            }
        }
        _userAnswers.value = initialMap
        _timeLeft.value = totalMinutes * 60
        startTimer()
    }

    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000)
                _timeLeft.value -= 1
            }
        }
    }

    fun handleSaveAndNext() {
        val sIdx = currentSectionIndex.value
        val qIdx = currentQuestionIndex.value
        val selected = tempSelection.value

        val newStatus = if (selected != null) Status.ANSWERED else Status.NOT_ANSWERED
        updateQuestionState(sIdx, qIdx, newStatus, selected)
        moveToNext()
    }

    fun handleMarkForReviewAndNext() {
        val sIdx = currentSectionIndex.value
        val qIdx = currentQuestionIndex.value
        val selected = tempSelection.value

        val newStatus = if (selected != null) Status.ANSWERED_AND_MARKED else Status.MARKED_FOR_REVIEW
        updateQuestionState(sIdx, qIdx, newStatus, selected)
        moveToNext()
    }

    fun handleClearResponse() {
        tempSelection.value = null
        updateQuestionState(currentSectionIndex.value, currentQuestionIndex.value, Status.NOT_ANSWERED, null)
    }

    private fun updateQuestionState(sIdx: Int, qIdx: Int, status: Status, selected: Int?) {
        val map = _userAnswers.value.toMutableMap()
        map[Pair(sIdx, qIdx)] = QuestionUserAnswer(status, selected)
        _userAnswers.value = map
    }

    private fun moveToNext() {
        val currentSec = _sections.value.getOrNull(currentSectionIndex.value) ?: return
        if (currentQuestionIndex.value < currentSec.questions.size - 1) {
            loadQuestion(currentSectionIndex.value, currentQuestionIndex.value + 1)
        } else if (currentSectionIndex.value < _sections.value.size - 1) {
            loadQuestion(currentSectionIndex.value + 1, 0)
        }
    }

    fun loadQuestion(sIdx: Int, qIdx: Int) {
        currentSectionIndex.value = sIdx
        currentQuestionIndex.value = qIdx
        val ans = _userAnswers.value[Pair(sIdx, qIdx)]
        tempSelection.value = ans?.selectedOption
        
        if (ans?.status == Status.NOT_VISITED) {
            updateQuestionState(sIdx, qIdx, Status.NOT_ANSWERED, null)
        }
    }
}
