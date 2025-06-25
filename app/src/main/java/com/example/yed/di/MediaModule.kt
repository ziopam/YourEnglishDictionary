package com.example.yed.di

import android.media.MediaPlayer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object MediaModule {

    @Provides
    @ViewModelScoped
    fun provideMediaPlayer(): MediaPlayer {
        return MediaPlayer()
    }
}