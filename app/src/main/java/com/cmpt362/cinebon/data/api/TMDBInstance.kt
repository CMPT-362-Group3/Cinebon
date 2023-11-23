package com.cmpt362.cinebon.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TMDBInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(TMDBService.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val movieService: TMDBService by lazy {
        retrofit.create(TMDBService::class.java)
    }

}