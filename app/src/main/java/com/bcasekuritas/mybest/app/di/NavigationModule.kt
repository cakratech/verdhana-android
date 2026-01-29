package com.bcasekuritas.mybest.app.di

import android.app.Activity
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import com.bcasekuritas.mybest.app.base.navigation.NavigationFinancial
import com.bcasekuritas.mybest.app.base.navigation.NavigationFullScreen
import com.bcasekuritas.mybest.app.base.navigation.NavigationMain
import com.bcasekuritas.mybest.app.base.navigation.NavigationMiddle
import com.bcasekuritas.mybest.ext.prefs.SharedPreferenceManager

@InstallIn(ActivityComponent::class)
@Module
object NavigationModule {
    @Provides
    fun providesNavigationMainModule(activity: Activity): NavigationMain {
        return NavigationMain(activity)
    }

    @Provides
    fun providesNavigationMidModule(activity: Activity): NavigationMiddle {
        return NavigationMiddle(activity)
    }

    @Provides
    fun providesNavigationFullScreenModule(activity: Activity): NavigationFullScreen {
        return NavigationFullScreen(activity)
    }

    @Provides
    fun providesNavigationFinancialModule(activity: Activity): NavigationFinancial {
        return NavigationFinancial(activity)
    }

    @Provides
    fun provideSharedPreferenceManager(@ApplicationContext context: Context): SharedPreferenceManager {
        return SharedPreferenceManager(context)
    }
}