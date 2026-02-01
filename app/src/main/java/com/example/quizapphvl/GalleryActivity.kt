package com.example.quizapphvl

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateListOf

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge() //I have commented this row, because if it's not commented, the screen will be covered from all the elements and some parts of the app will be hidden below the systems buttons, but I wonder if there is a better way to full the screen
        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Column(modifier = Modifier.fillMaxSize()) {
        ShowGallery(galleryItems, modifier = Modifier.weight(2.5f))
        ShowButtons()
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    QuizAppHvlTheme {
        Greeting("Android")
    }
}

@Composable
fun ShowGallery(items: List<GalleryItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        items(items) { item ->
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Column{
                    Image(
                        painter = painterResource(id = item.idImage),
                        contentDescription = item.name,
                        modifier = Modifier.size(160.dp)
                    )
                }
                Column{
                    Text("\n" + item.name + "'s flag")
                    Button(onClick = {removeFlag(item)}) {
                        Text("Remove flag")
                    }
                }
            }
        }
    }
}

@Composable
fun ShowButtons() {
    Row {
        Button(onClick = {addFlag()}, modifier = Modifier.weight(1f)) {
            Text("Add a new image")
        }
        Button(onClick = {sort1()}, modifier = Modifier.weight(1f)) {
            Text("Sort alphabetically")
        }
        Button(onClick = {sort2()}, modifier = Modifier.weight(1f)) {
            Text("Sort in the reverse alphabet")
        }
    }
}

/*TODO*/
fun addFlag() {}

/*This function removes the element selected by the user*/
fun removeFlag(item: GalleryItem) {
    galleryItems.remove(item)
}

/*This function sorts items in the aplhabetical order*/
fun sort1(){
    galleryItems.sortBy{
        it.name
    }
}

/*This function sorts items in the reverse alphabetical order*/
fun sort2(){
    galleryItems.sortByDescending {
        it.name
    }
}
