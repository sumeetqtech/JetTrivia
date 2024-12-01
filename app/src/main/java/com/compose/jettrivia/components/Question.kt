package com.compose.jettrivia.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compose.jettrivia.data.Results
import com.compose.jettrivia.model.QuestionItem
import com.compose.jettrivia.screens.QuestionViewModel
import com.compose.jettrivia.util.AppColors


@Composable
fun Question(modifier: Modifier = Modifier, viewModel: QuestionViewModel) {

    val questionIndex = remember {
        mutableIntStateOf(0)
    }

    when (val state = viewModel.data.value) {
        is Results.Loading -> {
            ProgressBar()
        }

        is Results.Success -> {
            val question = try {
                state.data[questionIndex.intValue]
            } catch (ex: Exception) {
                null
            }
            question?.let {
                QuestionDisplay(
                    modifier = modifier,
                    questionItem = it,
                    questionViewModel = viewModel,
                    questionIndex = questionIndex
                ) {
                    questionIndex.intValue += 1
                }
            }
        }

        is Results.Error -> Text(modifier = modifier, text = "Error: ${state.exception.message}")
    }
}

@Composable
fun QuestionDisplay(
    modifier: Modifier = Modifier,
    questionItem: QuestionItem,
    questionViewModel: QuestionViewModel,
    questionIndex: MutableState<Int>,
    onNextClicked: (Int) -> Unit
) {
    val choicesState = remember(questionItem) { questionItem.choices.toMutableList() }
    val answerState = remember(questionItem) { mutableStateOf<Int?>(null) }
    val correctAnswerState = remember(questionItem) { mutableStateOf<Boolean?>(null) }

    val updateAnswer: (Int) -> Unit = { selectedIndex ->
        answerState.value = selectedIndex
        correctAnswerState.value = choicesState[selectedIndex] == questionItem.answer
    }

    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 10f)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        color = AppColors.mDarkPurple
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            if (questionIndex.value >= 3) {
                ShowProgressIndicator(score = questionIndex.value)
            }
            QuestionTracker(
                counter = questionIndex.value + 1,
                outOf = questionViewModel.getTotalQuestionCount()
            )
            DottedLine(pathEffect)
            Spacer(modifier = Modifier.height(16.dp))
            Column {
                Text(
                    text = questionItem.question,
                    modifier = Modifier
                        .padding(8.dp)
                        .align(Alignment.Start)
                        .fillMaxHeight(0.2f)
                        .fillMaxWidth(),
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.mOffWhite
                )

                choicesState.forEachIndexed { index, answer ->
                    Row(
                        modifier = Modifier
                            .padding(4.dp)
                            .fillMaxWidth()
                            .height(48.dp)
                            .border(
                                width = 4.dp,
                                brush = Brush.linearGradient(
                                    colors = listOf(
                                        AppColors.mOffDarkPurple,
                                        AppColors.mOffDarkPurple
                                    )
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .clip(RoundedCornerShape(50))
                            .background(Color.Transparent),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (answerState.value == index),
                            onClick = { updateAnswer(index) },
                            modifier = Modifier.padding(16.dp),
                            colors = RadioButtonDefaults.colors(
                                selectedColor = when {
                                    correctAnswerState.value == true && index == answerState.value -> Color.Green.copy(
                                        alpha = 0.8f
                                    )

                                    correctAnswerState.value == false && index == answerState.value -> Color.Red.copy(
                                        alpha = 0.8f
                                    )

                                    else -> AppColors.mLightGray
                                }
                            )
                        )
                        Text(
                            text = answer,
                            color = AppColors.mOffWhite,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            Button(
                onClick = { onNextClicked(questionIndex.value) },
                modifier = Modifier
                    .padding(4.dp)
                    .align(Alignment.CenterHorizontally),
                shape = RoundedCornerShape(4.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.mLightBlue)
            ) {
                Text(
                    text = "Next",
                    modifier = Modifier.padding(4.dp),
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun DottedLine(pathEffect: PathEffect) {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .padding(vertical = 16.dp)
    ) {
        drawLine(
            color = AppColors.mLightGray,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}

@Composable
fun QuestionTracker(counter: Int = 1, outOf: Int = 10) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = ParagraphStyle(textIndent = TextIndent.None)) {
                withStyle(
                    style = SpanStyle(
                        color = AppColors.mLightGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp
                    )
                ) {
                    append("Question $counter/")
                    withStyle(
                        style = SpanStyle(
                            color = AppColors.mLightGray,
                            fontWeight = FontWeight.Light,
                            fontSize = 16.sp
                        )
                    ) {
                        append("$outOf")
                    }
                }
            }
        },
        modifier = Modifier.padding(4.dp)
    )
}

@Preview
@Composable
fun ShowProgressIndicator(
    modifier: Modifier = Modifier,
    score: Int = 12
) {

    val progressFactorState by remember(score) {
        mutableFloatStateOf(score * 0.005f)
    }

    val gradient = Brush.linearGradient(
        listOf(
            Color(0xFFF95075),
            Color(0xFFBE6BE5)
        )
    )

    Row(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .height(40.dp)
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    listOf(
                        AppColors.mLightPurple,
                        AppColors.mLightPurple
                    )
                ),
                shape = RoundedCornerShape(32.dp)
            )
            .clip(RoundedCornerShape(50))
            .background(Color.Transparent),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {},
            enabled = false,
            elevation = null,
            colors = buttonColors(
                containerColor = Color.Transparent,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Transparent
            ),
            contentPadding = PaddingValues(1.dp),
            modifier = Modifier
                .fillMaxWidth(progressFactorState)
                .background(brush = gradient)
        ) {
            Text(
                text = "${score * 10}",
                modifier = Modifier
                    .clip(shape = RoundedCornerShape(24.dp))
                    .fillMaxHeight(0.87f)
                    .fillMaxWidth()
                    .padding(8.dp),
                color = AppColors.mOffWhite,
                textAlign = TextAlign.Center
            )
        }
    }
}


