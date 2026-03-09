package com.example.quizapphvl

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.TypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.MutableLiveData
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * @param entities elements I want to store in the database
 */
@Database(entities = [GalleryItem1::class], version = 1)
@TypeConverters(Converters::class)
abstract class GalleryDatabase : RoomDatabase() {

    abstract fun imageDao(): ImageDao

    companion object {
        @Volatile
        private var INSTANCE: GalleryDatabase? = null

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

                    dao.insertImage(GalleryItem1(1, "Nauru", R.drawable.nr, null))
                    dao.insertImage(GalleryItem1(2, "Norway", R.drawable.no, null))
                    dao.insertImage(GalleryItem1(3, "French", R.drawable.fr, null))
                    dao.insertImage(GalleryItem1(4, "Mexico", R.drawable.mx, null))
                    dao.insertImage(GalleryItem1(5, "Spain", R.drawable.es, null))
                }
            }
        }
    }
}

@Entity(tableName = "image")
data class GalleryItem1(
    @PrimaryKey(autoGenerate = true) val idItem: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "idImage") val idDrawable: Int,
    @ColumnInfo(name = "imageUri") val imageUri: Uri?
)

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

// TODO: qua dovrei provare a cambiare tutto in modo tale che anziché fare tutto il calcolo per il random ha già il random che gli serve (e riceve quello da costruttore) in questo modo ho due viewModel separate però non si intralciano e soprattutto non ho bisogno di composable
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
        internal set
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


class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun toUri(value: String?): Uri? = value?.let { Uri.parse(it) }
}