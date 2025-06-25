package com.example.yed.data.repository

import com.example.yed.data.local.WordDao
import com.example.yed.data.local.WordEntity
import com.example.yed.domain.entity.Word
import com.example.yed.domain.repository.IWordRepository

class WordRepositoryImpl(private val dao: WordDao) : IWordRepository {
    override suspend fun addWord(word: Word) {
        dao.insert(WordEntity(word.word, word.phonetics, word.meanings))
    }

    override suspend fun getAllWords(): List<Word> =
        dao.getAll().map { Word(it.word, it.phonetics, it.meanings) }

    override suspend fun getAllToLearnWords(): List<Word> =
        dao.getToLearn().map { Word(it.word, it.phonetics, it.meanings) }

    override suspend fun isWordExists(wordText: String): Boolean {
        return dao.isExists(wordText)
    }

    override suspend fun markWordToLearn(wordText: String) {
        dao.markToLearn(wordText)
    }

    override suspend fun markWordAsLearned(wordText: String) {
        dao.markAsLearned(wordText)
    }

    override suspend fun deleteWord(wordText: String) {
        dao.delete(wordText)
    }

    override suspend fun deleteAllWords() {
        dao.deleteAll()
    }
}