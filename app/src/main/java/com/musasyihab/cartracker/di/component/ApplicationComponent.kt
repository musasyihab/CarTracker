package com.musasyihab.cartracker.di.component

import com.musasyihab.cartracker.BaseApp
import com.musasyihab.cartracker.di.module.ApplicationModule
import dagger.Component

@Component(modules = arrayOf(ApplicationModule::class))
interface ApplicationComponent {

    fun inject(application: BaseApp)

}