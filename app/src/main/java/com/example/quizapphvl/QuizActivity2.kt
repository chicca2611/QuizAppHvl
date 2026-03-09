/*package com.example.quizapphvl

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizapphvl.GalleryItem
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme

class QuizActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting5(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting5(name: String, modifier: Modifier = Modifier) {
    val model: MyElementsViewModel = viewModel()
    val allElements by model.allImages.observeAsState(listOf())
    Log.d("QQQQ", "Sono in QuizActivity2, stampo gli elementi: " + allElements)
    if(allElements.size <= 3)
        Text("Sorry, but there aren't sufficient elements to play the game. Please, add at least " + (3 - galleryItems.size) + " elements to play")
    else
        prova(model, allElements)
}

@Composable
fun prova(model: MyElementsViewModel, allElements: List<GalleryItem1>) {
    var item1 = allElements.random()
    Row {
        Column {
            if(item1.idDrawable == -1) {
                val context = LocalContext.current
                val bitmap = item1.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
                if (bitmap != null)
                    Image(
                        bitmap = bitmap,
                        contentDescription = item1.name,
                        modifier = Modifier.size(160.dp))
            }
            else {
                Image(
                    painter = painterResource(id = item1.idDrawable),
                    contentDescription = item1.name,
                    modifier = Modifier.size(180.dp)
                )
            }
            Text(model.mexFeedback)
            Text("Your actual score is: " + model.score + " / " + model.attempts)
        }
        val itemsToGuess = mutableListOf<GalleryItem1>()
        itemsToGuess.add(item1)
        var item2 : GalleryItem1
        var item3 : GalleryItem1

        do {  //pick casually an item from the list until it will be different from the item1
            item2 = allElements.random()
        } while (item1.idDrawable == item2.idDrawable)

        do {
            item3 = allElements.random()
        } while (item1.idDrawable == item3.idDrawable || item2.idDrawable == item3.idDrawable)

        itemsToGuess.add(item2)
        itemsToGuess.add(item3)
        itemsToGuess.shuffle()
        model.elementsToGuess = itemsToGuess
        ButtonSection()
    }

fun updateState(items: List<GalleryItem1>) {
    Log.d("QQQQ", "entrata in updateState")
    var correctItem : GalleryItem1
    if(attempts == 0) {
        correctItem = items.random()
    }
    else {
        do {
            correctItem = items.random()
            Log.d("QQQQ", "loop")
        } while(correctItem.idItem == itemCorrect.idItem)
    }

    val elementsToGuess = mutableListOf<GalleryItem1>()
    elementsToGuess.add(correctItem)

    var item2: GalleryItem1
    var item3: GalleryItem1

    do {  //pick casually an item from the list until it will be different from the item1
        item2 = items.random()
    } while (correctItem.idItem == item2.idItem)

    do {
        item3 = items.random()
    } while (correctItem.idItem == item3.idItem || item2.idItem == item3.idItem)
    Log.d("QQQQ", "finiti gli item update")
    elementsToGuess.add(item2)
    elementsToGuess.add(item3)
    elementsToGuess.shuffle()

    this.itemCorrect = correctItem
    this.item2 = item2
    this.item3 = item3
    this.elementsToGuess = elementsToGuess
}
@Composable
fun StartQuiz(model: MyElementsViewModel, allElements: List<GalleryItem1>) {
    LaunchedEffect(allElements) {
        model.updateState(allElements)
    }
    Row {
        Column {
            val context = LocalContext.current
            val itemCorrect = model.itemCorrect
            if(itemCorrect.idDrawable == -1) {
                val context = LocalContext.current
                val bitmap = model.itemCorrect.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = model.itemCorrect.name,
                        modifier = Modifier.size(160.dp))
                }
            }
            else {
                Image(
                    painter = painterResource(id = model.itemCorrect.idDrawable),
                    contentDescription = model.itemCorrect.name,
                    modifier = Modifier.size(180.dp)
                )
            }
            Text(model.mexFeedback)
            Text("Your actual score is: " + model.score + " / " + model.attempts)

            Button(onClick = {
                buttonToGuessPressed1(model.itemCorrect, model.elementsToGuess[0], model, allElements)
            },
                modifier = Modifier.weight(1f)) {
                Text(model.elementsToGuess[0].name)
            }
            Button(onClick = {
                buttonToGuessPressed1(model.itemCorrect, model.elementsToGuess[1], model, allElements)
            },
                modifier = Modifier.weight(1f)) {
                Text(model.elementsToGuess[1].name)
            }
            Button(onClick = {
                buttonToGuessPressed1(model.itemCorrect, model.elementsToGuess[2], model, allElements)
            },
                modifier = Modifier.weight(1f)) {
                Text(model.elementsToGuess[2].name)
            }
        }
    }
}
    ButtonSection() {

    }
fun buttonToGuessPressed1(itemCorrect: GalleryItem1, elementToGuess: GalleryItem1, model: MyElementsViewModel, allElements: List<GalleryItem1>) {
    model.attempts++
    var itemCorrectName = itemCorrect.name
    if(verifyAnswer(itemCorrect, elementToGuess)) {
        model.score++
        model.mexFeedback = "Correct! Good job. Let's try again? :)"
    }
    else
        model.mexFeedback= "Ops, this is the wrong answer. The correct answer was $itemCorrectName"

    model.updateState(allElements)
}
/**
 * @param itemCorrect: this is the item that the user should guess
 * @param item: this is the item that the user has choose
 *
 * @return true if the id of the item is the same of itemCorrect, false otherwise
 * */
fun verifyAnswer(itemCorrect: GalleryItem1, item: GalleryItem1) : Boolean {
    if(itemCorrect == item)
        return true
    return false
}*/
