package com.example.yed.domain.entity

data class Word(
    val word: String,
    val phonetics: List<Phonetic>,
    val meanings: List<Meaning>,
)

data class Phonetic(
    val text: String? = null,
    val audio: String? = null,
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>,
)

data class Definition(
    val definition: String,
    val example: String? = null
)