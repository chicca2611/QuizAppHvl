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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme
import kotlin.collections.shuffle
import androidx.activity.viewModels

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("QQQQ", "I'm inside the onCreate function after the super.onCreate")
        val model: MyElements by viewModels<MyElements>()

        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting2(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding),
                        model
                    )
                }
            }
        }
    }
}

class MyElements : ViewModel {

    constructor() {
        this.updateState(galleryItems)
        Log.d("QQQQ", "I'm inside the constructor of viewModel")
    }

    var score = mutableIntStateOf(0)
    var attempts = mutableIntStateOf(0)
    var mexFeedback = mutableStateOf("Guess the flag")

    var elementsToGuess = mutableListOf<GalleryItem>()
        private set
    lateinit var itemCorrect: GalleryItem
        private set
    lateinit var item2 : GalleryItem
        private set
    lateinit var item3 : GalleryItem
       private set

    fun updateState(items: List<GalleryItem>) {
        Log.d("QQQQ", "entrata in updateState")
        var correctItem : GalleryItem
        if(attempts.intValue == 0) {
            correctItem = items.random()
        }
        else {
            do {
                correctItem = items.random()
                Log.d("QQQQ", "loop")
            } while(correctItem.idImage == itemCorrect.idImage)
        }

        val elementsToGuess = mutableListOf<GalleryItem>()
        elementsToGuess.add(correctItem)

        var item2: GalleryItem
        var item3: GalleryItem

        do {  //pick casually an item from the list until it will be different from the item1
            item2 = items.random()
        } while (correctItem.idImage == item2.idImage)

        do {
            item3 = items.random()
        } while (correctItem.idImage == item3.idImage || item2.idImage == item3.idImage)
        Log.d("QQQQ", "finiti gli item update")
        elementsToGuess.add(item2)
        elementsToGuess.add(item3)
        elementsToGuess.shuffle()

        this.itemCorrect = correctItem
        this.item2 = item2
        this.item3 = item3
        this.elementsToGuess = elementsToGuess
    }
}

@Composable
fun Greeting2(name: String, modifier: Modifier = Modifier, model: MyElements) {

    Log.d("QQQQ", "value of size: " + galleryItems.size)
    if(galleryItems.size<3)
        Text("Sorry, but there aren't sufficient elements to play the game. Please, add at least " + (3 - galleryItems.size) + " elements to play")
    else
        ShowImageToGuess(galleryItems, model)
}

/**
 * @param items: the complete list of the items contented in the gallery of the application
 */
//@SuppressLint("AutoboxingStateValueProperty")
@Composable
fun ShowImageToGuess(items: List<GalleryItem>, model: MyElements) {
    Row{
        Column {
                if(model.itemCorrect.idImage <= -1) { //the item chose casually was added from the user, so it has to be loaded in a different way
                    val context = LocalContext.current
                    val bitmap = model.itemCorrect.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
                    if(bitmap == null)
                        Log.d("QQQQ - ", "BITMAP E' NULL")
                    if (bitmap != null) {
                        Log.d("QQQQ - ", "bitmap non è NULL")
                        Image(
                            bitmap = bitmap,
                            contentDescription = model.itemCorrect.name,
                            modifier = Modifier.size(160.dp))
                    }
                }
                else {
                    Image(
                        painter = painterResource(id = model.itemCorrect.idImage),
                        contentDescription = model.itemCorrect.name,
                        modifier = Modifier.size(180.dp)
                    )
                }
                Text(model.mexFeedback.value)
                var printScore = model.score.intValue
                var printAttempts = model.attempts.intValue
                Text("your actual score is: $printScore/$printAttempts")

                Button(onClick = {
                    buttonToGuessPressed(model.itemCorrect, model.elementsToGuess[0], model)
                },
                    modifier = Modifier.weight(1f)) {
                    Text(model.elementsToGuess[0].name)
                }
                Button(onClick = {
                    buttonToGuessPressed(model.itemCorrect, model.elementsToGuess[1], model)
                },
                    modifier = Modifier.weight(1f)) {
                    Text(model.elementsToGuess[1].name)
                }
                Button(onClick = {
                    buttonToGuessPressed(model.itemCorrect, model.elementsToGuess[2], model)
                },
                    modifier = Modifier.weight(1f)) {
                    Text(model.elementsToGuess[2].name)
                }
        }
    }
}


fun buttonToGuessPressed(itemCorrect: GalleryItem?, elementToGuess: GalleryItem, model: MyElements) {
    model.attempts.intValue++
    var itemCorrectName = itemCorrect?.name
    if(verifyAnswer(itemCorrect, elementToGuess)) {
        model.score.intValue++
        model.mexFeedback.value = "Correct! Good job. Let's try again? :)"
    }
    else
        model.mexFeedback.value = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"

    model.updateState(galleryItems)
}

/**
 * @param itemCorrect: this is the item that the user should guess
 * @param item: this is the item that the user has choose
 *
 * @return true if the id of the item is the same of itemCorrect, false otherwise
 * */
fun verifyAnswer(itemCorrect: GalleryItem?, item: GalleryItem) : Boolean {
    if(itemCorrect?.idImage == item.idImage)
        return true
    return false
}

