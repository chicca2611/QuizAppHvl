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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.quizapphvl.ui.theme.QuizAppHvlTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

class GalleryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting1(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting1(name: String, modifier: Modifier = Modifier) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("GALLERY OF WORLD'S FLAGS", modifier = modifier.testTag("title"))
        val model: MyElementsViewModel = viewModel()
        val allElements by model.allImages.observeAsState(listOf())
        ShowGallery(allElements, modifier = Modifier.weight(2.5f), model)
    }
}

@Composable
fun ShowGallery(items: List<GalleryItem1>, modifier: Modifier = Modifier, model: MyElementsViewModel) {
    Text("Hello! You have: " + items.size + " images stored in your gallery", Modifier.testTag("numberElements"))
    if(items.isEmpty())
        Text("")
    else {
        var itemsToShow = items
        if(model.typeOfOrder == 1)
            itemsToShow = items.sortedBy { it.name }
        else if(model.typeOfOrder == 2)
            itemsToShow = items.sortedByDescending { it.name }
        else
            itemsToShow = items

        LazyColumn(modifier = modifier) {
            var counter = 0
            items(itemsToShow) { item ->
                counter++
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        val context = LocalContext.current
                        val bitmap =
                            item.imageUri?.let { getBitmapFromUriResource(context, it)?.asImageBitmap() }
                        if (bitmap != null) { //it means that the image that the lazy column is showing is an image added from the user
                            Image(
                                bitmap = bitmap,
                                contentDescription = item.name,
                                modifier = Modifier.size(160.dp)
                            )
                        } else {
                            Text("Sorry, something went wrong")
                        }
                    }
                    Column {
                        Text("\n" + item.name + "'s flag")
                        if(counter == 1)
                            Button(onClick = {removeFlag(item, model)}, Modifier.testTag("removeButton")) {
                                Text("Remove flag")
                            }
                        else
                            Button(onClick = {removeFlag(item, model)}) {
                                Text("Remove flag")
                            }
                    }
                }
            }
        }
    }
    Row {
        ShowButtons(model)
    }
}

fun removeFlag(item: GalleryItem1, model: MyElementsViewModel) {
    model.delete(item)
}

fun sort1(model: MyElementsViewModel) {
    model.typeOfOrder = 1
}

fun sort2(model: MyElementsViewModel) {
    model.typeOfOrder = 2
}

@Composable
fun ShowButtons(model: MyElementsViewModel) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(false) }
    var tempUri by remember { mutableStateOf<Uri?>(null) }
    var inputName by remember { mutableStateOf("") }

    val pickMedia = rememberLauncherForActivityResult(PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, flag)
            } catch (e: SecurityException) {
                Log.e("Gallery", "Not allowed persistence permission")
            }

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
                        modifier = Modifier.testTag("dialogInput"),
                        placeholder = { Text("") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inputName.isNotBlank() && tempUri != null) {
                        model.insert(GalleryItem1(0, inputName, tempUri))
                        showDialog = false
                        inputName = ""
                        tempUri = null
                    }
                }, Modifier.testTag("confirmButton")) {
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
        }, modifier = Modifier.weight(1f).testTag("addingImage")) {
            Text("Add a new image")
        }
        Button(onClick = {sort1(model)}, modifier = Modifier.weight(1f)) {
            Text("Sort alphabetically")
        }
        Button(onClick = {sort2(model)}, modifier = Modifier.weight(1f)) {
            Text("Sort in the reverse alphabet")
        }
    }
}