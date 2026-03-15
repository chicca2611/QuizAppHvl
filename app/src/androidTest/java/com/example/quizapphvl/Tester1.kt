package com.example.quizapphvl

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Tester1 {

    @get:Rule
    val composeTestRule = createComposeRule()
    
    //https://developer.android.com/training/testing/espresso/basics?hl=it

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()



    @Test
    fun testClickToGallery() {
        onView(withId(R.id.buttonGallery))
            .perform(click())

        composeTestRule.onNodeWithText("GALLERY OF WORLD'S FLAGS")
            .assertIsDisplayed()

    }
}
