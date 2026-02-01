package com.example.quizapphvl

import androidx.compose.runtime.mutableStateListOf

data class GalleryItem(
    val name: String,
     val idImage: Int)


val galleryItems = mutableStateListOf(
    GalleryItem("French", R.drawable.fr),
    GalleryItem("Spain", R.drawable.es),
    GalleryItem("Norway", R.drawable.no),
    GalleryItem("Nauru", R.drawable.nr)
)