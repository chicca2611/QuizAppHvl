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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

//var numberImages: Int = 0

class GalleryActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // enableEdgeToEdge()
        setContent {
            QuizAppHvlTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting4(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting4(name: String, modifier: Modifier = Modifier) {

    Column(modifier = Modifier.fillMaxSize()) {
        Text("GALLERY OF WORLD'S FLAGS", modifier = modifier.testTag("title"))
        val model: MyElementsViewModel = viewModel()
        Log.d("QQQQ", "quante volte chiamo questo metodo?")
        val allElements by model.allImages.observeAsState(listOf())
        ShowGallery1(allElements, modifier = Modifier.weight(2.5f), model)
        Log.d("QQQQ", "Vorrei mostrare i miei allEmements:$allElements")
    }
}

@Composable
fun ShowGallery1(items: List<GalleryItem1>, modifier: Modifier = Modifier, model: MyElementsViewModel) {
    //numberImages = items.size
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
            Log.d("QQQQ", "appena entrata in ShowGallery()$items")
            var counter = 0
            items(itemsToShow) { item ->
                counter++
                Log.d("QQQQ", "Ci sono item da scorrere")
                Row(
                    modifier = Modifier.padding(8.dp)
                ) {
                    Column {
                        val context = LocalContext.current
                        /*if(item.idDrawable == -2) {
                            //Image(PainterResource(id = item.idDrawable), item.name, Modifier.size(160.dp))
                            Image(
                                painter = painterResource(id = item.imageUri),
                                contentDescription = item.name,
                                modifier = Modifier.size(160.dp)
                            )
                            Image()
                        }
                        if(item.idDrawable == -2) {
                            val bitmap =
                                item.imageUri?.let { getBitmapFromUriResource(context, it)?.asImageBitmap() }
                            Image(
                                bitmap = bitmap,
                                contentDescription = item.name,
                                modifier = Modifier.size(160.dp)
                            )
                        }*/
                        val bitmap =
                            item.imageUri?.let { getBitmapFromUriResource(context, it)?.asImageBitmap() }
                        if (bitmap != null) { //it means that the image that the lazy column is showing is an image added from the user
                            Log.d("QQQQ - ", "bitmap non è NULL")
                            Image(
                                bitmap = bitmap,
                                contentDescription = item.name,
                                modifier = Modifier.size(160.dp)
                            )
                        } else {
                            Log.d("QQQQ", "default image")
                            Image(
                                painter = painterResource(id = item.idDrawable),
                                contentDescription = item.name,
                                modifier = Modifier.size(160.dp)
                            )
                        }
                    }
                    Column {
                        Text("\n" + item.name + "'s flag")
                        if(counter == 1)
                            Button(onClick = {removeFlag1(item, model)}, Modifier.testTag("removeButton")) {
                                Text("Remove flag")
                            }
                        else
                            Button(onClick = {removeFlag1(item, model)}) {
                                Text("Remove flag")
                            }
                    }
                }
            }
            Log.d("QQQQ", "Ho finito di scorrere gli item")
        }
    }
    Row {
        ShowButtons1(model)
    }
}

fun removeFlag1(item: GalleryItem1, model: MyElementsViewModel) {
    model.delete(item)
}

fun sort_1(model: MyElementsViewModel) {
    Log.d("QQQQ", "I'm calling the sort function")
    model.typeOfOrder = 1
    Log.d("QQQQ", "I finished to sort")
}

fun sort_2(model: MyElementsViewModel) {
    model.typeOfOrder = 2
}

@Composable
fun ShowButtons1(model: MyElementsViewModel) {
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
                Log.e("Gallery", "Permesso persistente non concesso (normale per i test o risorse locali)")
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
                        model.insert(GalleryItem1(0, inputName, -1, tempUri))
                        //model.negId--
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
        Button(onClick = {sort_1(model)}, modifier = Modifier.weight(1f)) {
            Text("Sort alphabetically")
        }
        Button(onClick = {sort_2(model)}, modifier = Modifier.weight(1f)) {
            Text("Sort in the reverse alphabet")
        }
    }
}