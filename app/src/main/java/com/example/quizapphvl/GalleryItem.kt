package com.example.quizapphvl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

/***
 * @param name: the name associated to the image (it is supposed be a name of a Country)
 * @param idImage: univoque id associable to the image, it can be negative or R.drawable
 * @param imageUri: Uri linked to the images added from the gallery of the user, it could be null
 * */
data class GalleryItem(
    val name: String,
    val idImage: Int,
    val imageUri: Uri? = null
)

class MyItems : ViewModel() {
    val galleryItems = mutableStateListOf(
        GalleryItem("French", R.drawable.fr),
        GalleryItem("Spain", R.drawable.es),
        GalleryItem("Norway", R.drawable.no),
        GalleryItem("Mexico", R.drawable.mx),
        GalleryItem("Nauru", R.drawable.nr)
    )

    fun removeAt(index: Int) {
        galleryItems.removeAt(index)
    }
}

/***
 * This is a default set of images
 * */

val galleryItems = mutableStateListOf(
    GalleryItem("French", R.drawable.fr),
    GalleryItem("Spain", R.drawable.es),
    GalleryItem("Norway", R.drawable.no),
    GalleryItem("Mexico", R.drawable.mx),
    GalleryItem("Nauru", R.drawable.nr)
)

/**
 * @param uri
 * This function opens a bitmap file given the realitve uri
 *
 * sources:
 * https://developer.android.com/training/data-storage/shared/documents-files?hl=it#bitmap
 *
 * https://developer.android.com/training/data-storage/shared/photo-picker?hl=it
 * */
fun getBitmapFromUri(context: Context, uri: Uri): Bitmap? {
    Log.d("QQQQ", "" + uri)
    val parcelFileDescriptor = context.contentResolver.openFileDescriptor(uri, "r") ?: return null
    Log.d("QQQQ", "parcelFileDescriptor success")
    val bitmap = BitmapFactory.decodeFileDescriptor(parcelFileDescriptor.fileDescriptor)
    Log.d("QQQQ", "bitmap transforming success")
    parcelFileDescriptor.close()
    Log.d("QQQQ","returning bitmap")
    return bitmap
}


