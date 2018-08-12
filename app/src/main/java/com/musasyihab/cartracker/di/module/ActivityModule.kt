package com.musasyihab.cartracker.di.module

import android.app.Activity
import com.musasyihab.cartracker.ui.main.MainContract
import com.musasyihab.cartracker.ui.main.MainPresenter
import dagger.Module
import dagger.Provides

@Module
class ActivityModule(private var activity: Activity) {

    @Provides
    fun provideActivity(): Activity {
        return activity
    }

    @Provides
    fun providePresenter(): MainContract.Presenter {
        return MainPresenter()
    }

}