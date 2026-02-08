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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme

class QuizActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
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
    Log.d("DEBUG BY FEDER - ", "value of size: " + galleryItems.size)
    if(galleryItems.size<3)
        Text("Sorry, but there aren't sufficient elements to play the game. Please, add at least " + (3 - galleryItems.size) + " elements to play")
    else
        ShowImageToGuess(galleryItems)
}

/**
 * @param items: the complete list of the items contented in the gallery of the application
 */
@Composable
fun ShowImageToGuess(items: List<GalleryItem>) {
    var item1 = items.random() //this will be the item linked to the image to guess

    // the following three variables are saved with by remember because we want to mantain their state during the execution of the application
    var score by remember { mutableIntStateOf(0) }
    var attempts by remember {mutableIntStateOf(0)}
    var mexFeedback by remember {mutableStateOf("Guess the flag.")}

    Row{
        Column {
            if(item1.idImage <= -1) { //the item chose casually was added from the user, so it has to be loaded in a different way
                val context = LocalContext.current
                val bitmap = item1.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
                if(bitmap == null)
                    Log.d("QQQQ - ", "BITMAP E' NULL")
                if (bitmap != null) {
                    Log.d("QQQQ - ", "bitmap non è NULL")
                    Image(
                        bitmap = bitmap,
                        contentDescription = item1.name,
                        modifier = Modifier.size(160.dp))
                }
            }
            else {
                Image(
                    painter = painterResource(id = item1.idImage),
                    contentDescription = item1.name,
                    modifier = Modifier.size(180.dp)
                )
            }
            Text(mexFeedback)
            Text("Your actual score is: $score/$attempts")



            val elementsToGuess = mutableListOf<GalleryItem>()
            elementsToGuess.add(item1)
            var item2: GalleryItem
            var item3: GalleryItem

            do {  //pick casually an item from the list until it will be different from the item1
                item2 = items.random()
            } while (item1.idImage == item2.idImage)

            do {
                item3 = items.random()
            } while (item1.idImage == item3.idImage || item2.idImage == item3.idImage)

            elementsToGuess.add(item2)
            elementsToGuess.add(item3)
            elementsToGuess.shuffle()

            var itemCorrectName = item1.name

            Button(onClick = {
                attempts++
                if (verifyAnswer(item1, elementsToGuess[0])) {
                    score++
                    mexFeedback = "Correct! Good job. Let's try again? :)"
                }
                else
                    mexFeedback = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"
            },
                modifier = Modifier.weight(1f)) {
                Text(elementsToGuess[0].name)
            }
            Button(onClick = {
                attempts++
                if (verifyAnswer(item1, elementsToGuess[1])){
                    score++
                    mexFeedback = "Correct! Good job. Let's try again? :)"
                }
                else
                    mexFeedback = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"
            },
                modifier = Modifier.weight(1f)) {
                Text(elementsToGuess[1].name)
            }
            Button(onClick = {
                attempts++
                if (verifyAnswer(item1, elementsToGuess[2])){
                    score++
                    mexFeedback = "Correct! Good job. Let's try again? :)"
                }
                else
                    mexFeedback = "Ops, this is the wrong answer. The correct answer was $itemCorrectName"
            },
                modifier = Modifier.weight(1f)) {
                Text(elementsToGuess[2].name)
            }
        }
    }
}

/**
 * @param itemCorrect: this is the item that the user should guess
 * @param item: this is the item that the user has choose
 *
 * @return true if the id of the item is the same of itemCorrect, false otherwise
 * */
fun verifyAnswer(itemCorrect: GalleryItem, item: GalleryItem) : Boolean {
    if(itemCorrect.idImage == item.idImage)
        return true
    return false
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    QuizAppHvlTheme {
        Greeting2("Android")
    }
}