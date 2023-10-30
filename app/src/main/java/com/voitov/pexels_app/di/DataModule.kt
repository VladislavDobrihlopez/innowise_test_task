package com.voitov.pexels_app.di

import com.voitov.pexels_app.data.datasource.LocalDataSource
import com.voitov.pexels_app.data.datasource.LocalDataSourceImpl
import com.voitov.pexels_app.data.datasource.RemoteDataSource
import com.voitov.pexels_app.data.datasource.RemoteDataSourceImpl
import com.voitov.pexels_app.data.network.ApiService
import com.voitov.pexels_app.data.network.ApiService.Companion.BASE_URL
import com.voitov.pexels_app.data.network.ApiService.Companion.CUSTOM_AUTH_HEADER
import com.voitov.pexels_app.data.network.ApiService.Companion.TOKEN
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataModule {
    @Singleton
    @Binds
    abstract fun bindLocalDataSource(impl: LocalDataSourceImpl): LocalDataSource

    @Singleton
    @Binds
    abstract fun bindPexelsInterface(impl: RemoteDataSourceImpl): RemoteDataSource

    companion object {
        @Singleton
        @Provides
        fun provideOkHttp() =
            OkHttpClient.Builder().addInterceptor { chain ->
                val sourceRequest = chain.request()
                val modifiedRequest =
                    sourceRequest.newBuilder().header(
                        CUSTOM_AUTH_HEADER,
                        TOKEN
                    ).build()
                chain.proceed(modifiedRequest)
            }.build()

        @Singleton
        @Provides
        fun provideRetrofit(httpsClient: OkHttpClient) = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpsClient)
            .build()

        @Singleton
        @Provides
        fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)
    }
}