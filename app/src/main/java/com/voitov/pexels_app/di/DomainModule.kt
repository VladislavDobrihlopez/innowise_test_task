package com.voitov.pexels_app.di

import com.voitov.pexels_app.data.datasource.LocalDataSource
import com.voitov.pexels_app.data.datasource.LocalDataSourceImpl
import com.voitov.pexels_app.data.datasource.RemoteDataSource
import com.voitov.pexels_app.data.repository.PexelsRepositoryImpl
import com.voitov.pexels_app.domain.PexelsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class DomainModule {
    @Binds
    abstract fun bindPexelsInterface(impl: PexelsRepositoryImpl): PexelsRepository
}