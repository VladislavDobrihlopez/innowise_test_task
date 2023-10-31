package com.voitov.pexels_app.di

import android.content.Context
import com.voitov.pexels_app.data.database.PexelsDatabase
import com.voitov.pexels_app.data.database.dao.FeaturedCollectionsDao
import com.voitov.pexels_app.data.database.dao.PhotosDao
import com.voitov.pexels_app.data.datasource.cache.HotCacheDataSource
import com.voitov.pexels_app.data.datasource.cache.entity.PhotoDetailsCacheEntity
import com.voitov.pexels_app.data.datasource.cache.impl.FeaturedCollectionsCacheImpl
import com.voitov.pexels_app.data.datasource.cache.impl.PhotosCacheImpl
import com.voitov.pexels_app.data.datasource.local.LocalDataSource
import com.voitov.pexels_app.data.datasource.local.LocalDataSourceImpl
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSource
import com.voitov.pexels_app.data.datasource.remote.RemoteDataSourceImpl
import com.voitov.pexels_app.data.network.ApiService
import com.voitov.pexels_app.data.network.ApiService.Companion.BASE_URL
import com.voitov.pexels_app.data.network.ApiService.Companion.CUSTOM_AUTH_HEADER
import com.voitov.pexels_app.data.network.ApiService.Companion.TOKEN
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.FeaturedCache
import com.voitov.pexels_app.di.annotation.PhotosCache
import com.voitov.pexels_app.domain.model.FeaturedCollection
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
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
    abstract fun bindPexelsRemoteDataSource(impl: RemoteDataSourceImpl): RemoteDataSource

    @PhotosCache
    @Singleton
    @Binds
    abstract fun bindPhotosCache(impl: PhotosCacheImpl): HotCacheDataSource<Int, PhotoDetailsCacheEntity, String>

    @FeaturedCache
    @Singleton
    @Binds
    abstract fun bindFeaturedCollectionsCache(impl: FeaturedCollectionsCacheImpl): HotCacheDataSource<String, FeaturedCollection, Nothing>

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
        fun provideDatabase(@ApplicationContext context: Context): PexelsDatabase {
            return PexelsDatabase.getInstance(context)
        }

        @Provides
        fun providePhotoDao(database: PexelsDatabase): PhotosDao {
            return database.getPhotoDao()
        }

        @Provides
        fun provideFeaturedCollectionsDao(database: PexelsDatabase): FeaturedCollectionsDao {
            return database.getFeaturedCollectionsDao()
        }

        @Singleton
        @Provides
        fun provideApiService(retrofit: Retrofit) = retrofit.create(ApiService::class.java)

        @Provides
        fun provideAppScope(
            @DispatcherIO dispatcher: CoroutineDispatcher
        ): CoroutineScope {
            return CoroutineScope(SupervisorJob() + dispatcher)
        }
    }
}