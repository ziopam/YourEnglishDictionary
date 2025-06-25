package com.example.yed.di

import android.content.Context
import androidx.room.Room
import com.example.yed.data.local.WordDao
import com.example.yed.data.local.WordDatabase
import com.example.yed.data.repository.WordRepositoryImpl
import com.example.yed.domain.repository.IWordRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DBModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context) : WordDatabase{
        return Room.databaseBuilder(
            context,
            WordDatabase::class.java,
            "word_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideWordDao(db: WordDatabase): WordDao = db.wordDao()

    @Provides
    @Singleton
    fun provideWordRepository(dao: WordDao): IWordRepository = WordRepositoryImpl(dao)
}