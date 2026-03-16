package com.example.quizapphvl

import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class QuizTest {
    @get:Rule
    val composeQuizTestRule = createAndroidComposeRule<QuizActivity>()

    @Test
    fun correctScoreForCorrectAnswer() {
        composeQuizTestRule
            .onNodeWithTag("correctButton")
            .performClick()

        composeQuizTestRule
            .onNodeWithTag("textWithScore")
            .assertTextEquals("your actual score is: 1/1")
    }

    @Test
    fun correctScoreForWrongAnswer() {
        composeQuizTestRule
            .onNodeWithTag("wrongButton")
            .performClick()

        composeQuizTestRule
            .onNodeWithTag("textWithScore")
            .assertTextEquals("your actual score is: 0/1")
    }
}