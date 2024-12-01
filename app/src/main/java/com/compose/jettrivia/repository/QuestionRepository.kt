package com.compose.jettrivia.repository

import com.compose.jettrivia.data.Results
import com.compose.jettrivia.model.QuestionItem
import com.compose.jettrivia.network.QuestionApi
import javax.inject.Inject

class QuestionRepository @Inject constructor(private val questionApi: QuestionApi) {

    suspend fun getQuestions(): Results<List<QuestionItem>> {
        return try {
            Results.Loading
            val questions = questionApi.getAllQuestions()
            Results.Success(questions)
        } catch (e: Exception) {
            Results.Error(e)
        }
    }

}