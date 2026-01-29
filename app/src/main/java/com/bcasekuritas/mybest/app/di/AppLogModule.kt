package com.bcasekuritas.mybest.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
object AppLogModule {

//    @Provides
//    @ViewModelScoped
//    fun provideLogProvider(@ApplicationContext context: Context): LoggerManager {
//        return LoggerManager(context)
//    }
}