package com.voitov.pexels_app.di

import com.voitov.pexels_app.data.repository.BookmarkedPhotosRepositoryImpl
import com.voitov.pexels_app.data.repository.FeaturedCollectionsRepositoryImpl
import com.voitov.pexels_app.data.repository.PhotosRepositoryImpl
import com.voitov.pexels_app.domain.repository.PexelsBookmarkedPhotosRepository
import com.voitov.pexels_app.domain.repository.PexelsFeaturedCollectionsRepository
import com.voitov.pexels_app.domain.repository.PexelsPhotosRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DomainModule {
    @Singleton
    @Binds
    abstract fun bindPhotosRepository(repository: PhotosRepositoryImpl): PexelsPhotosRepository

    @Singleton
    @Binds
    abstract fun bindFeaturedCollectionsRepository(repository: FeaturedCollectionsRepositoryImpl): PexelsFeaturedCollectionsRepository

    @Singleton
    @Binds
    abstract fun bindBookmarkedPhotosRepository(repository: BookmarkedPhotosRepositoryImpl): PexelsBookmarkedPhotosRepository
}