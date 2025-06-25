package com.example.yed.domain.repository

import com.example.yed.domain.entity.Word

interface IWordRepository {
    suspend fun addWord(word: Word)
    suspend fun getAllWords(): List<Word>
    suspend fun isWordExists(wordText: String): Boolean
    suspend fun getAllToLearnWords(): List<Word>
    suspend fun markWordToLearn(wordText: String)
    suspend fun markWordAsLearned(wordText: String)
    suspend fun deleteWord(wordText: String)
    suspend fun deleteAllWords()
}