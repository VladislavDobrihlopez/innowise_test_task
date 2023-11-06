package com.voitov.pexels_app.di.annotation

import javax.inject.Qualifier

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DispatcherIO

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DispatcherDefault

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DispatcherMain

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class DispatcherMainImmediate