package com.example.quizapphvl

import android.os.Bundle
import android.util.Log
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.viewmodel.compose.viewModel

class QuizActivity3 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("QQQQ", "I'm inside the onCreate function after the super.onCreate")

        //val model: MyElements3 by viewModels<MyElements3>()

        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting6(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


@Composable
fun Greeting6(name: String, modifier: Modifier = Modifier) {
    val parentModel : MyElementsViewModel = viewModel()
    val allElements by parentModel.allImages.observeAsState(listOf())
    if(allElements.size < 3)
        Text("Sorry, you don't have enough elements to can do the quiz, please, add new flags.")
    else {
        val modelQuiz : MyElementsQuizViewModel = viewModel()
        ShowImageToGuess3(modelQuiz)
    }

}


@Composable
fun ShowImageToGuess3(model: MyElementsQuizViewModel) {
    val items = model.itemsIWantToGuess
    if (items == null || items.size < 3) {
        Text("Loading")
        return
    }
    Row {
        Column {
           val context = LocalContext.current

            val bitmap = model.itemCorrect?.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
            if(bitmap != null) {
                Image(
                    bitmap = bitmap,
                    contentDescription = model.itemCorrect!!.name,
                    modifier = Modifier.size(160.dp))
            }
            else {
                Image(
                    painter = painterResource(id = model.itemCorrect?.idDrawable ?: R.drawable.ic_launcher_foreground ),
                    contentDescription = model.itemCorrect?.name,
                    modifier = Modifier.size(180.dp)
                )
            }
            Text(model.mexFeedback)
            val printScore = model.score
            val printAttempts = model.attempts
            Text("your actual score is: $printScore/$printAttempts")
            Button(onClick = {
                buttonToGuessPressed3(model.itemCorrect, items[0], model)
            },
                modifier = Modifier.weight(1f)) {
                Text(items[0].name)
            }
            Button(onClick = {
                buttonToGuessPressed3(model.itemCorrect, items[1], model)
            },
                modifier = Modifier.weight(1f)) {
                Text(items[1].name)
            }
            Button(onClick = {
                buttonToGuessPressed3(model.itemCorrect, items[2], model)
            },
                modifier = Modifier.weight(1f)) {
                Text(items[2].name)
            }
        }

    }
}


fun buttonToGuessPressed3(itemCorrect: GalleryItem1?, elementToGuess: GalleryItem1, model: MyElementsQuizViewModel) {
    model.attempts++
    var itemCorrectName = itemCorrect?.name
    if(verifyAnswer3(itemCorrect, elementToGuess)) {
        model.score++
        model.mexFeedback = "Correct! Good job. Let's try again? :)"
    }
    else
        model.mexFeedback = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"

    model.updateState3()
}

/**
 * @param itemCorrect: this is the item that the user should guess
 * @param item: this is the item that the user has choose
 *
 * @return true if the id of the item is the same of itemCorrect, false otherwise
 * */
fun verifyAnswer3(itemCorrect: GalleryItem1?, item: GalleryItem1) : Boolean {
    if(itemCorrect?.idDrawable == item.idDrawable)
        return true
    return false
}
