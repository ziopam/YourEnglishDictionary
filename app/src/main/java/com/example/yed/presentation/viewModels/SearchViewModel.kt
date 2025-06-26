package com.example.yed.presentation.viewModels

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yed.data.ApiService
import com.example.yed.domain.entity.Phonetic
import com.example.yed.domain.entity.Word
import com.example.yed.domain.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URL
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    val mediaPlayer: MediaPlayer,
    private val repository: IWordRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {
    private val filesDir = context.filesDir

    private val isLoading = mutableStateOf(false)
    val loadingState: State<Boolean> = isLoading

    private val currentWord = mutableStateOf(null as Word?)
    val wordState: State<Word?> = currentWord

    private var _errorMessage: String? = null
    val errorMessage: String? get() = _errorMessage

    private var _wordSavingState = mutableStateOf(WordSavingState.NotSaved as WordSavingState)
    val wordSavingState: State<WordSavingState> = _wordSavingState


    fun getWordDefinition(word: String) {
        if(word.isBlank()){
            return
        }

        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = apiService.getWord(word)
                if (response.isSuccessful) {
                    val wordList = response.body()
                    if (wordList != null && wordList.isNotEmpty()) {
                         if(repository.isWordExists(wordList[0].word)) {
                            _wordSavingState.value = WordSavingState.Saved
                        } else {
                            _wordSavingState.value = WordSavingState.NotSaved
                        }

                        currentWord.value = fixPhonetics(wordList[0])
                        _errorMessage = null
                    } else {
                        currentWord.value = null
                        _errorMessage = "No results found for '$word'."
                    }
                } else {
                    if (response.code() == 404) {
                        currentWord.value = null
                        _errorMessage = "No results found for '$word'."
                    } else {
                        currentWord.value = null
                        _errorMessage = "Internal server error. Please try again later."
                    }
                }
            } catch (_: Exception) {
                currentWord.value = null
                _errorMessage = "Please check your internet connection and try again."
            } finally {
                isLoading.value = false
            }
        }
    }

    private fun fixPhonetics(word: Word): Word {
        val phonetics = word.phonetics.filter { !it.text.isNullOrBlank() }
            .let { filtered ->
                val uk = filtered.firstOrNull { it.audio?.contains("uk", ignoreCase = true) == true }
                val us = filtered.firstOrNull { it.audio?.contains("us", ignoreCase = true) == true }

                val preferred = listOfNotNull(uk, us).distinct()

                if (preferred.size < 2) {
                    val remaining = filtered.filterNot { it == uk || it == us }
                    (preferred + remaining.take(2 - preferred.size)).take(2)
                } else {
                    preferred
                }
            }
        return word.copy(phonetics = phonetics)
    }

    fun addWordToDictionary() {
        if (currentWord.value != null && _wordSavingState != WordSavingState.Saved) {
            viewModelScope.launch(Dispatchers.IO) {
                _wordSavingState.value = WordSavingState.Loading
                val phonetics = currentWord.value!!.phonetics
                val downloadedAudios = mutableListOf<Phonetic>()

                for (phonetic in phonetics) {
                    val audioUrl = phonetic.audio
                    if (!audioUrl.isNullOrBlank()) {
                        val fileName = audioUrl.substringAfterLast("/")
                        val result = downloadAudioAndSaveLocally(audioUrl, fileName)

                        if (result.isFailure) {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Check your Internet connection", Toast.LENGTH_SHORT).show()
                            }
                            _wordSavingState.value = WordSavingState.NotSaved
                            return@launch
                        } else {
                            val localPath = result.getOrNull()
                            downloadedAudios.add(phonetic.copy(audio = localPath))
                        }
                    } else {
                        downloadedAudios.add(phonetic)
                    }
                }

                val updatedWord = currentWord.value!!.copy(phonetics = downloadedAudios)
                repository.addWord(updatedWord)
                _wordSavingState.value = WordSavingState.Saved
            }
        }
    }

    private suspend fun downloadAudioAndSaveLocally(url: String, fileName: String): Result<String> {
        val file = File(filesDir, fileName)
        return try {
            withContext(Dispatchers.IO) {
                URL(url).openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
            }
            Result.success(file.absolutePath)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun removeWordFromDictionary() {
        if (currentWord.value != null && wordSavingState.value == WordSavingState.Saved) {
            viewModelScope.launch(Dispatchers.IO) {
                _wordSavingState.value = WordSavingState.Loading
                repository.deleteWord(currentWord.value!!.word)
                _wordSavingState.value = WordSavingState.NotSaved
            }
        }
    }
}