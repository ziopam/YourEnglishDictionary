package com.example.yed.presentation.viewModels

import android.media.MediaPlayer
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yed.domain.entity.Word
import com.example.yed.domain.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val repository: IWordRepository,
    val mediaPlayer: MediaPlayer
) : ViewModel() {

    private val _wordQueue = mutableListOf<Word>()

    private val _currentWord = mutableStateOf<Word?>(null)
    val currentWord: State<Word?> = _currentWord

    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading

    private val _isFinished = mutableStateOf(false)
    val isFinished: State<Boolean> = _isFinished

    fun loadWordsToLearn() {        
        _isLoading.value = true
        
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val words = repository.getAllToLearnWords()
                _wordQueue.clear()
                _wordQueue.addAll(words)
                
                if (_wordQueue.isNotEmpty()) {
                    _currentWord.value = _wordQueue[0]
                    _isFinished.value = false
                } else {
                    _currentWord.value = null
                    _isFinished.value = true
                }
            } catch (_: Exception) {
                _currentWord.value = null
                _isFinished.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onSwipeLeft() {
        _currentWord.value?.let { word ->
            viewModelScope.launch(Dispatchers.IO) {
                removeCurrentWordAndShowNext()
            }
        }
    }

    fun onSwipeRight() {
        _currentWord.value?.let { word ->
            _wordQueue.remove(word)
            _wordQueue.add(word)
            showNextWord()
        }
    }

    private fun removeCurrentWordAndShowNext() {
        _currentWord.value?.let { word ->
            _wordQueue.remove(word)
            showNextWord()
        }
    }

    private fun showNextWord() {
        if (_wordQueue.isNotEmpty()) {
            _currentWord.value = _wordQueue[0]
            _isFinished.value = false
        } else {
            _currentWord.value = null
            _isFinished.value = true
        }
    }

    fun resetLearning() {
        loadWordsToLearn()
    }
}