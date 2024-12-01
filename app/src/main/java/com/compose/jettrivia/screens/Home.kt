package com.compose.jettrivia.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.compose.jettrivia.components.Question


@Composable
fun Home(
    modifier: Modifier = Modifier, viewModel: QuestionViewModel = hiltViewModel()
) {
    Question(modifier, viewModel)
}