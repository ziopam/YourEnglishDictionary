package com.example.yed.data

import com.example.yed.domain.entity.Word
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path


interface ApiService {
    @GET("api/v2/entries/en/{word}")
    suspend fun getWord(@Path("word") word: String): Response<List<Word>>
}