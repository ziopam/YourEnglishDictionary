package com.example.yed.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.yed.domain.entity.Meaning
import com.example.yed.domain.entity.Phonetic

@Entity(tableName = "words")
data class WordEntity(
    @PrimaryKey val word: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>,
    val isToLearn: Boolean = true,
)
