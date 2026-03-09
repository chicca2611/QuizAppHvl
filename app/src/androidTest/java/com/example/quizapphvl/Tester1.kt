package com.example.quizapphvl
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class Tester1 {

    companion object {
        private const val PROVA =
    }

    //https://developer.android.com/training/testing/espresso/basics?hl=it
    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()


    @get:Rule
    val composeGalleryTestRule = createAndroidComposeRule<GalleryActivity2>()
    @Test
    fun testClickToGallery() {
        onView(withId(R.id.buttonGallery))
            .perform(click())
        Thread.sleep(2500)
        composeGalleryTestRule.onNodeWithText("GALLERY OF WORLD'S FLAGS")
            .assertIsDisplayed()
    }

    @Test
    fun testClickToGalleryFalse() {
        onView(withId(R.id.buttonGallery))
            .perform(click())
        Thread.sleep(2500)
        composeGalleryTestRule.onNodeWithTag(("GALLERYY OF WORLD'S FLAGS"))
            .assertIsNotDisplayed()
    }
    @Test
    fun correctScore() {
        composeQuizTestRule.
    }
}