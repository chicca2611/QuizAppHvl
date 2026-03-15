package com.example.quizapphvl

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * @param PrimaryKey - id autogenerate and unique
 * @param name - name of the flag
 * @param idDrawable - id of the image if it's from the resource (not more needed)
 * @param imageUri - uri of the image to show
 * */
@Entity(tableName = "image")
data class GalleryItem1(
    @PrimaryKey(autoGenerate = true) val idItem: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "idImage") val idDrawable: Int,
    @ColumnInfo(name = "imageUri") val imageUri: Uri?
)



/**
 * This class creates the database
 */
@Database(entities = [GalleryItem1::class], version = 1) //version = 1 to save from crash if in the future the entities of the db will be modified
@TypeConverters(Converters::class)
abstract class GalleryDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: GalleryDatabase? = null

        /**
         * @return INSTANCE - unique instance of the database
         * */
        fun getDatabase(context: Context, scope: CoroutineScope): GalleryDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GalleryDatabase::class.java,
                    "gallery_database"
                )
                    .addCallback(GalleryDatabaseCallback())
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }

    private class GalleryDatabaseCallback() : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)

            INSTANCE?.let { database ->
                CoroutineScope(Dispatchers.IO).launch {
                    val dao = database.imageDao()

                    dao.insertImage(GalleryItem1(1, "Nauru", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.nr}")))
                    dao.insertImage(GalleryItem1(2, "Spain", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.es}")))
                    dao.insertImage(GalleryItem1(3, "Norway", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.no}")))
                    dao.insertImage(GalleryItem1(4, "French", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.fr}")))
                    dao.insertImage(GalleryItem1(5, "Mexico", -1, Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.mx}")))


                    //dao.insertImage(GalleryItem1(1, "Nauru", -1, Uri.parse("android.resource://com.example.quizapphvl/drawable/nr")))
                    //dao.insertImage(GalleryItem1(1, "Nauru", R.drawable.nr, null))
                    //dao.insertImage(GalleryItem1(2, "Norway", -1,  Uri.parse("android.resource://com.example.quizapphvl/" + R.drawable.no)))
                    //dao.insertImage(GalleryItem1(3, "French", R.drawable.fr, Uri.parse((R.drawable.fr).toString())))
                }
            }
        }
    }
}



/**
 * This interface contains the methods to log in the database
 * */
@Dao
interface ImageDao {
    @Insert
    suspend fun insertImage(image: GalleryItem1)

    @Delete
    suspend fun delete(image: GalleryItem1)

    @Query("SELECT * FROM image")
    fun getAll(): LiveData<List<GalleryItem1>>

    @Query("SELECT COUNT (*) FROM image")
    fun getNumberItems(): LiveData<Int>

    @Query("SELECT DISTINCT * FROM image ORDER BY RANDOM() LIMIT 3")
    suspend fun getRandomImage(): List<GalleryItem1>
}


class ImageRepository(private val imageDao: ImageDao) {
    var allImages: LiveData<List<GalleryItem1>> = imageDao.getAll()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun insertImage(newImage: GalleryItem1) {
        coroutineScope.launch(Dispatchers.IO) {
            imageDao.insertImage(newImage)
        }
    }

    fun deleteImage(image: GalleryItem1) {
        coroutineScope.launch(Dispatchers.IO) {
            imageDao.delete(image)
        }
    }

    suspend fun getRandomItems(): List<GalleryItem1> {
        return imageDao.getRandomImage()
    }
}


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
        //negId--
    }

    fun delete(image: GalleryItem1) = viewModelScope.launch {
        imageRepository.deleteImage(image)
    }
}

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
            updateState3()
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
    fun updateState3() {
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

fun getBitmapFromUriResource(context: Context, uri: Uri?): Bitmap? {
    if (uri == null) return null

    return try {
        // Uri from resorurce
        if (uri.scheme == "android.resource") {
            // prova a convertire il lastPathSegment in resource id
            val resId = uri.lastPathSegment?.toIntOrNull()
            if (resId != null) {
                return BitmapFactory.decodeResource(context.resources, resId)
            }
        }

        // Uri from Gallery
        context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
            BitmapFactory.decodeFileDescriptor(pfd.fileDescriptor)
        }

    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun toUri(value: String?): Uri? = value?.let { Uri.parse(it) }
}
