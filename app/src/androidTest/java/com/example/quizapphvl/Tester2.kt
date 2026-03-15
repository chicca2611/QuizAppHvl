package com.example.quizapphvl

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test

class Tester2 {
    @get:Rule
    val composeQuizTestRule = createAndroidComposeRule<QuizActivity3>()

    @Test
    fun correctScoreForCorrectAnswer() {
        //composeQuizTestRule. // se viene schiacciato il bottone con tag iditemcorrect, allora score deve essere uguale a se stesso + 1? altrimenti deve rimanere uguale a se stesso e msotrare messaggio di errore.
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