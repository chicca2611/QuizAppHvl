package com.example.quizapphvl

import android.app.Instrumentation
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import android.app.Activity
import android.net.Uri
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.anyIntent
import androidx.test.espresso.intent.Intents
import org.junit.After
import org.junit.Before

/*
A test that checks that the number of registered pictures/persons is correct after adding/deleting an entry.
For adding, use Intent Stubbling to return some image data (es. from the resource-folder)
without any user interaction

* */
class Tester3 {
    @get:Rule
    val composeGalleryTestRule = createAndroidComposeRule<GalleryActivity2>()

   // var numberImagesTest = 0
    @Before
    fun setUp() {
        //numberImagesTest = numberImages
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }


   //@get:Rule
   // val intentsTestRule = IntentsTestRule(GalleryActivity2::class.java)

    @Test
    fun addingImageTest() {
        val resultData = Intent()
        val image = Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.test}")
        //val image = GalleryItem1(6, "Test", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.test}"))
        resultData.data = image
        val result = Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
        //intending(toPackage("com.android.gallery")).respondWith(result)
        intending(anyIntent()).respondWith(result)

        composeGalleryTestRule
            .onNodeWithTag("addingImage")
            .performClick()

        //composeGalleryTestRule.waitForIdle()

        composeGalleryTestRule
            .onNodeWithTag("dialogInput")
            .performTextInput("Test")

        composeGalleryTestRule
            .onNodeWithTag("confirmButton")
            .performClick()

        composeGalleryTestRule
            .onNodeWithTag("numberElements")
            .assertTextEquals("Hello! You have: 6 images stored in your gallery")
    }

    @Test
    fun removingImageTest() {
        composeGalleryTestRule
            .onNodeWithTag("removeButton")
            .performClick()

        composeGalleryTestRule
            .onNodeWithTag("numberElements")
            .assertTextEquals("Hello! You have: 4 images stored in your gallery")
    }
}