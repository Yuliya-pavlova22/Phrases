package org.hyperskill.phrases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Phrase::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getDao(): PhraseDao

    companion object{
        fun getDB(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "phrases.db"
            ).allowMainThreadQueries().build()
        }
    }
}
