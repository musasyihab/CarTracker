package com.musasyihab.cartracker.di.module

import android.app.Application
import com.musasyihab.cartracker.BaseApp
import com.musasyihab.cartracker.di.scope.PerApplication
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class ApplicationModule(private val baseApp: BaseApp) {

    @Provides
    @Singleton
    @PerApplication
    fun provideApplication(): Application {
        return baseApp
    }
}