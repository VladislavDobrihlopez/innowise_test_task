package com.voitov.pexels_app.di

import com.voitov.pexels_app.di.annotation.DispatcherDefault
import com.voitov.pexels_app.di.annotation.DispatcherIO
import com.voitov.pexels_app.di.annotation.DispatcherMain
import com.voitov.pexels_app.di.annotation.DispatcherMainImmediate
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@InstallIn(SingletonComponent::class)
@Module
class CoroutineDispatchersModule {
    @DispatcherIO
    @Provides
    fun provideDispatcherIO(): CoroutineDispatcher = Dispatchers.IO

    @DispatcherDefault
    @Provides
    fun provideDispatcherDefault(): CoroutineDispatcher = Dispatchers.Default

    @DispatcherMain
    @Provides
    fun provideDispatcherMain(): CoroutineDispatcher = Dispatchers.Main

    @DispatcherMainImmediate
    @Provides
    fun provideDispatcherMainImmediate(): CoroutineDispatcher = Dispatchers.Main.immediate
}