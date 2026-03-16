package com.example.quizapphvl

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
 * @param imageUri - uri of the image to show
 * */
@Entity(tableName = "image")
data class GalleryItem1(
    @PrimaryKey(autoGenerate = true) val idItem: Int = 0,
    @ColumnInfo(name = "name") val name: String,
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

                    dao.insertImage(GalleryItem1(1, "Nauru", Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.nr}")))
                    dao.insertImage(GalleryItem1(2, "Spain", Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.es}")))
                    dao.insertImage(GalleryItem1(3, "Norway", Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.no}")))
                    dao.insertImage(GalleryItem1(4, "French", Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.fr}")))
                    dao.insertImage(GalleryItem1(5, "Mexico", Uri.parse("android.resource://com.example.quizapphvl/${R.drawable.mx}")))
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

class Converters {
    @TypeConverter
    fun fromUri(uri: Uri?): String? = uri?.toString()

    @TypeConverter
    fun toUri(value: String?): Uri? = value?.let { Uri.parse(it) }
}
