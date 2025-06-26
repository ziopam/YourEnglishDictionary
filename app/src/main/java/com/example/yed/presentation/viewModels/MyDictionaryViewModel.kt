package com.example.yed.presentation.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yed.domain.entity.Word
import com.example.yed.domain.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyDictionaryViewModel @Inject constructor(
    private val repository: IWordRepository
) : ViewModel() {
    private val isLoading = mutableStateOf(false)
    val loadingState: State<Boolean> = isLoading

    private val _currentWords = mutableStateListOf<Word>()
    val currentWords: SnapshotStateList<Word> get() = _currentWords

    fun loadWords() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val words = repository.getAllWords()
            _currentWords.clear()
            _currentWords.addAll(words)
            isLoading.value = false
        }
    }

    fun markWordToLearn(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markWordToLearn(word.word)
        }
    }

    fun markWordAsLearned(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.markWordAsLearned(word.word)
        }
    }

    fun deleteWord(word: Word) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteWord(word.word)
            _currentWords.remove(word)
        }
    }

}