package com.example.quizapphvl

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx. compose. ui. graphics. asImageBitmap
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableIntStateOf


var negId by mutableIntStateOf(-1) //global variable to assign when is added a new Image from the gallery of the user, it will be negative ever, in this way, it will be impossible having same id with the R drawable library

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
        Log.d("DEBUG FEDER",
            "quante volte chiamo questo metodo?, allora qua mi andrebbe di stampare: $galleryItems"
        )
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
/**
 * @param items a List of GalleryItem elements
 * @param Modifier to modify with attributes expressed in the call of the function
 */
@Composable
fun ShowGallery(items: List<GalleryItem>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        Log.d("DEBUG FEDER", "appena entrata in ShowGallery()$items")
        items(items) { item ->
            Row(
                modifier = Modifier.padding(8.dp)
            ) {
                Column{
                    val context = LocalContext.current
                    val bitmap = item.imageUri?.let { getBitmapFromUri(context, it)?.asImageBitmap() }
                    if (bitmap != null) { //it means that the image that the lazy column is showing is an image added from the user
                        Log.d("DEBUG FEDER - ", "bitmap non è NULL")
                        Image(
                            bitmap = bitmap,
                            contentDescription = item.name,
                            modifier = Modifier.size(160.dp))
                    }
                    else {
                        Image(
                            painter = painterResource(id = item.idImage),
                            contentDescription = item.name,
                            modifier = Modifier.size(160.dp)
                        )
                    }
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
/**
 * This Composable function permits to see the buttons for the actions we want to do in the gallery activity and it shows the dialog window to give a name for every new image the user wants to add
 * */
@Composable
fun ShowButtons() {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var inputName by remember { mutableStateOf("") }

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            context.contentResolver.takePersistableUriPermission(uri, flag)

            tempUri = uri
            showDialog = true
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Image's name") },
            text = {
                Column {
                    TextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        singleLine = true,
                        placeholder = { Text("") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputName.isNotBlank() && tempUri != null) {
                        galleryItems.add(GalleryItem(
                            name = inputName,
                            idImage = negId,
                            imageUri = tempUri
                        ))
                        negId--
                        showDialog = false
                        inputName = ""
                        tempUri = null
                    }
                }) {
                    Text("add Image")
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("go back")
                }
            }
        )
    }
    Row {
        Button(onClick = {
            pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }, modifier = Modifier.weight(1f)) {
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


