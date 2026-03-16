package com.example.quizapphvl

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


/**
 * This viewModel handles the quiz logic.
 * */
class MyElementsQuizViewModel(application: Application): AndroidViewModel(application) {
    private val imageRepository: ImageRepository
    var itemsIWantToGuess: List<GalleryItem1>? by mutableStateOf(null)
    init {
        val dao = GalleryDatabase.getDatabase(application, viewModelScope).imageDao()
        imageRepository = ImageRepository(dao)
        viewModelScope.launch {
            updateState()
        }
    }

    var score by mutableIntStateOf(0)
    var attempts by mutableIntStateOf(0)
    var mexFeedback by mutableStateOf("Guess the flag")

    var itemCorrect: GalleryItem1? by mutableStateOf(null)
        private set
    var item2: GalleryItem1? by mutableStateOf(null)
        private set
    var item3: GalleryItem1? by mutableStateOf(null)
        private set
    var previousItem: GalleryItem1? by mutableStateOf(null)
        private set
    fun updateState() {
        viewModelScope.launch {
            itemsIWantToGuess = imageRepository.getRandomItems()
            if(itemCorrect != null) {
                previousItem = itemCorrect!!
            }
            itemCorrect = itemsIWantToGuess?.get(0)
            if(previousItem?.idItem != itemsIWantToGuess?.get(0)?.idItem) {
                itemCorrect = itemsIWantToGuess?.get(0)
                item2 = itemsIWantToGuess?.get(1)
            }
            else { //in this way, will not happen ever that the same flag will show two times near
                itemCorrect = itemsIWantToGuess?.get(1)
                item2 = itemsIWantToGuess?.get(0)
            }
            item3 = itemsIWantToGuess?.get(2)

            itemsIWantToGuess = itemsIWantToGuess?.shuffled()
        }
    }
}
