package com.example.quizapphvl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier) {
    val parentModel : MyElementsViewModel = viewModel()
    val allElements by parentModel.allImages.observeAsState(listOf())
    if(allElements.size < 3)
        Text("Sorry, you don't have enough elements to can do the quiz, please, add new flags.")
    else {
        val modelQuiz : MyElementsQuizViewModel = viewModel()
        ShowImageToGuess(modelQuiz)
    }

}


@Composable
fun ShowImageToGuess(model: MyElementsQuizViewModel) {
    val items = model.itemsIWantToGuess
    if (items == null || items.size < 3) {
        Text("Loading")
        return
    }
    Row {
        Column {
           val context = LocalContext.current

            val bitmap = model.itemCorrect?.imageUri?.let { getBitmapFromUriResource(context, it)?.asImageBitmap() }
            if(bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = model.itemCorrect!!.name,
                    modifier = Modifier.size(160.dp))
            }
            else {
                Text("sorry, something is gone wrong")
            }
            Text(model.mexFeedback)
            val printScore = model.score
            val printAttempts = model.attempts
            Text("your actual score is: $printScore/$printAttempts", Modifier.testTag("textWithScore"))
            var tag1 = ""
            var tag2 = ""
            var tag3 = ""
            if(model.itemCorrect?.idItem == items[0].idItem) {
                tag1 = "correctButton"
                tag2 = "wrongButton"
            }
            else if(model.itemCorrect?.idItem == items[1].idItem) {
                tag1 = "wrongButton"
                tag2 = "correctButton"
            }
            else {
                tag1 = "wrongButton"
                tag3 = "correctButton"
            }
            Button(onClick = {
                buttonToGuessPressed(model.itemCorrect, items[0], model)
            },
                modifier = Modifier.weight(1f).testTag(tag1)) {
                Text(items[0].name)
            }

            Button(onClick = {
                buttonToGuessPressed(model.itemCorrect, items[1], model)
            },
                modifier = Modifier.weight(1f).testTag(tag2)) {
                Text(items[1].name)
            }

            Button(onClick = {
                buttonToGuessPressed(model.itemCorrect, items[2], model)
            },
                modifier = Modifier.weight(1f).testTag(tag3)) {
                Text(items[2].name)
            }
        }

    }
}


fun buttonToGuessPressed(itemCorrect: GalleryItem1?, elementToGuess: GalleryItem1, model: MyElementsQuizViewModel) {
    model.attempts++
    var itemCorrectName = itemCorrect?.name
    if(verifyAnswer(itemCorrect?.idItem ?: -1, elementToGuess.idItem)) {
        model.score++
        model.mexFeedback = "Correct! Good job. Let's try again? :)"
    }
    else
        model.mexFeedback = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"

    model.updateState()
}

/**
 * @param itemCorrect: this is the id item that the user should guess
 * @param item: this is the id item that the user has choose
 *
 * @return true if the id of the item is the same of itemCorrect, false otherwise
 * */
fun verifyAnswer(itemCorrectId: Int, itemId: Int) : Boolean {
    if(itemCorrectId == itemId)
        return true
    return false
}

fun getBitmapFromUriResource(context: Context, uri: Uri?): Bitmap? {
    if (uri == null) return null

    return try {
        if (uri.scheme == "android.resource") {
            val resId = uri.lastPathSegment?.toIntOrNull()
            if (resId != null) {
                return BitmapFactory.decodeResource(context.resources, resId)
            }
        }

        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}