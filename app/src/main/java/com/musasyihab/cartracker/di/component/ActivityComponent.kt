package com.musasyihab.cartracker.di.component

import com.musasyihab.cartracker.ui.main.MainActivity
import com.musasyihab.cartracker.di.module.ActivityModule
import dagger.Component

@Component(modules = arrayOf(ActivityModule::class))
interface ActivityComponent {

    fun inject(mainActivity: MainActivity)

}