package com.example.yed.presentation.viewModels

import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yed.data.ApiService
import com.example.yed.domain.entity.Word
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val apiService: ApiService,
    val mediaPlayer: MediaPlayer
) : ViewModel() {
    private val isLoading = mutableStateOf(false)
    val loadingState: State<Boolean> = isLoading

    private val currentWord = mutableStateOf(null as Word?)
    val wordState: State<Word?> = currentWord

    private var _errorMessage: String? = null
    val errorMessage: String? get() = _errorMessage


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
}