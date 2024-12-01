package com.compose.jettrivia.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.compose.jettrivia.data.Results
import com.compose.jettrivia.model.QuestionItem
import com.compose.jettrivia.repository.QuestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuestionViewModel @Inject constructor(private val questionRepository: QuestionRepository) :
    ViewModel() {

    private val _data: MutableState<Results<List<QuestionItem>>> =
        mutableStateOf(Results.Loading)
    val data: State<Results<List<QuestionItem>>> get() = _data

    init {
        getAllQuestion()
    }

    private fun getAllQuestion() {
        viewModelScope.launch {
            _data.value = Results.Loading
            try {
                val result = questionRepository.getQuestions()
                _data.value = result
            } catch (e: Exception) {
                _data.value = Results.Error(e)
            }
        }
    }

    fun getTotalQuestionCount(): Int {
        return when (val result = data.value) {
            is Results.Success -> result.data.toMutableList().size
            else -> 0
        }
    }
}



