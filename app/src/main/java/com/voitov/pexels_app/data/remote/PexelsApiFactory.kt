package com.voitov.pexels_app.data.remote

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object PexelsApiFactory {
    private const val BASE_URL = "https://api.pexels.com/"
    private const val CUSTOM_AUTH_HEADER = "Authorization"
    const val TOKEN = "E0Yymn8ICVbKaroPC0JOarSlaK09gXT177Ykixqfa05prWL4PBB1Rc4D"

    private val okHttpClient =
        OkHttpClient.Builder().addInterceptor { chain ->
            val sourceRequest = chain.request()
            val modifiedRequest =
                sourceRequest.newBuilder().header(CUSTOM_AUTH_HEADER, TOKEN).build()
            chain.proceed(modifiedRequest)
        }.build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)
        .build()
}