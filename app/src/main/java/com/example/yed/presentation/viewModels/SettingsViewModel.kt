package com.example.yed.presentation.viewModels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yed.domain.repository.IWordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: IWordRepository
) : ViewModel() {

    private val _isDeleting = mutableStateOf(false)
    val isDeleting: State<Boolean> = _isDeleting

    fun deleteAllWords() {
        _isDeleting.value = true
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteAllWords()
            _isDeleting.value = false
        }
    }
}
