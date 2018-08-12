package com.musasyihab.cartracker.ui.main

import com.musasyihab.cartracker.model.AvailabilityModel
import com.musasyihab.cartracker.model.LocationModel
import com.musasyihab.cartracker.ui.base.BaseContract

class MainContract {

    interface View: BaseContract.View {
        fun showProgress(show: Boolean)
        fun showErrorMessage(error: String)
        fun loadDataSuccess(list: List<LocationModel>)
        fun loadAvailability(list: List<AvailabilityModel>)
    }

    interface Presenter: BaseContract.Presenter<MainContract.View> {
        fun loadLocations()
        fun checkAvailability(startTime: Long, endTime: Long)
    }
}