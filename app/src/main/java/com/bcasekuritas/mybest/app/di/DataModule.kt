package com.bcasekuritas.mybest.app.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import com.bcasekuritas.mybest.app.data.dao.*
import com.bcasekuritas.mybest.app.data.db.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // ROOM
    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Singleton
    @Provides
    fun provideSourceDao(appDatabase: AppDatabase): SourceDao = appDatabase.sourceDao()

    @Singleton
    @Provides
    fun provideAccountDao(appDatabase: AppDatabase): AccountDao = appDatabase.accountDao()

    @Singleton
    @Provides
    fun provideStockParamDao(appDatabase: AppDatabase): StockParamDao = appDatabase.stockParamDao()

    @Singleton
    @Provides
    fun provideSessionDao(appDatabase: AppDatabase): SessionDao = appDatabase.sessionDao()

    @Singleton
    @Provides
    fun provideOrderReplyDao(appDatabase: AppDatabase): OrderReplyDao = appDatabase.orderReplyDao()

    @Singleton
    @Provides
    fun provideBiometricDao(appDatabase: AppDatabase): BiometricDao = appDatabase.biometricDao()

    @Singleton
    @Provides
    fun provideStockNotationDao(appDatabase: AppDatabase): StockNotationDao = appDatabase.stockNotationDao()

    @Singleton
    @Provides
    fun provideFilterDao(appDatabase: AppDatabase): FilterDao = appDatabase.filterDao()

}