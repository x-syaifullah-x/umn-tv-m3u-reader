package id.xxx.example.data.source.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [M3uEntity::class],
    version = 1,
    exportSchema = false
)
abstract class M3uDB : RoomDatabase() {

    companion object {

        @Volatile
        private var instance: M3uDB? = null

        fun getInstance(context: Context?) =
            instance ?: synchronized(M3uDB::class.java) {
                context ?: throw NullPointerException()
//                instance ?: Room.databaseBuilder(
//                    context.applicationContext,
//                    M3uDB::class.java,
//                    "id.xxx.fake.gps"
//                )
                instance ?: Room.inMemoryDatabaseBuilder(
                    context.applicationContext,
                    M3uDB::class.java
                )
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }

    abstract fun m3uDao(): M3uDao
}