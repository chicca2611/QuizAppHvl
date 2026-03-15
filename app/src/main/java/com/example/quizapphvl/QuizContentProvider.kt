package com.example.quizapphvl

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class QuizContentProvider : ContentProvider() {

    override fun query(
        p0: Uri,
        p1: Array<out String?>?,
        p2: String?,
        p3: Array<out String?>?,
        p4: String?
    ): Cursor? {
        val db = GalleryDatabase
            .getDatabase(context!!, CoroutineScope(Dispatchers.IO))
            .openHelper
            .readableDatabase

        return db.query("SELECT name, imageUri FROM image")
    }


    override fun delete(
        p0: Uri,
        p1: String?,
        p2: Array<out String?>?
    ): Int {
        return 0
    }

    override fun getType(p0: Uri): String? {
        return null
    }

    override fun insert(p0: Uri, p1: ContentValues?): Uri? {
        return null
    }

    override fun onCreate(): Boolean {
        return true
    }


    override fun update(
        p0: Uri,
        p1: ContentValues?,
        p2: String?,
        p3: Array<out String?>?
    ): Int {
        return 0
    }

}