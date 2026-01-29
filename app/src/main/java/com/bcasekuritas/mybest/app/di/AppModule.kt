package com.bcasekuritas.mybest.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.bcasekuritas.mybest.BcasApp
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplication(@ApplicationContext app: Context)
    : BcasApp = app as BcasApp

    @Singleton
    @Provides
    fun provideContext(@ApplicationContext context: BcasApp)
    : Context = context

}