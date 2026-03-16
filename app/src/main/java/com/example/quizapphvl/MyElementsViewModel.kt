package com.example.quizapphvl

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * This viewModel handles the logic of the gallery activity
 * */
class MyElementsViewModel(application: Application): AndroidViewModel(application) {
    var typeOfOrder by mutableIntStateOf(0)
    private val imageRepository: ImageRepository
    var allImages: LiveData<List<GalleryItem1>>
    init {
        val dao = GalleryDatabase.getDatabase(application, viewModelScope).imageDao()
        imageRepository = ImageRepository(dao)
        allImages = imageRepository.allImages
    }

    fun insert(image: GalleryItem1) = viewModelScope.launch {
        imageRepository.insertImage(image)
    }

    fun delete(image: GalleryItem1) = viewModelScope.launch {
        imageRepository.deleteImage(image)
    }
}
