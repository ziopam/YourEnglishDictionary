package com.example.yed.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface WordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(word: WordEntity)

    @Query("SELECT * FROM words")
    suspend fun getAll(): List<WordEntity>

    @Query("SELECT * FROM words WHERE isToLearn = 1")
    suspend fun getToLearn(): List<WordEntity>

    @Query("UPDATE words SET isToLearn = 1 WHERE word = :wordText")
    suspend fun markToLearn(wordText: String)

    @Query("UPDATE words SET isToLearn = 0 WHERE word = :wordText")
    suspend fun markAsLearned(wordText: String)

    @Query("SELECT EXISTS(SELECT 1 FROM words WHERE word = :wordText)")
    suspend fun isExists(wordText: String): Boolean

    @Query("DELETE FROM words WHERE word = :wordText")
    suspend fun delete(wordText: String)

    @Query("DELETE FROM words")
    suspend fun deleteAll()
}