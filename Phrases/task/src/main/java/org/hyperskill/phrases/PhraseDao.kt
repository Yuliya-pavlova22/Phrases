package org.hyperskill.phrases


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PhraseDao {
    @Insert
    fun insertItem(item: Phrase)

    @Delete
    fun deleteItem(item: Phrase)

    @Query("SELECT * FROM phrases")
    fun getAll(): List<Phrase>

    @Query("SELECT * FROM phrases limit 1")
    fun get(): Phrase

    @Query("SELECT COUNT(*) FROM phrases")
    fun getCount(): Int

}