package com.example.yed.presentation.viewModels

sealed class WordSavingState {
    object NotSaved : WordSavingState()
    object Loading : WordSavingState()
    object Saved : WordSavingState()
}