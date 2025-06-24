package com.example.yed.presentation.viewModels

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
    private val apiService: ApiService
) : ViewModel() {
    private val isLoading = mutableStateOf(false)
    val loadingState: State<Boolean> = isLoading

    private val currentWord = mutableStateOf(null as Word?)
    val wordState: State<Word?> = currentWord

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
                        currentWord.value = wordList[0]
                    } else {
                        currentWord.value = null
                    }
                } else {
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                println(e.message)
            } finally {
                isLoading.value = false
            }

        }


    }
}