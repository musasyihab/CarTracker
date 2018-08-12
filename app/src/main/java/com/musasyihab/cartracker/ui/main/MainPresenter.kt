package com.musasyihab.cartracker.ui.main

import com.musasyihab.cartracker.api.ApiServiceInterface
import com.musasyihab.cartracker.model.AvailabilityListModel
import com.musasyihab.cartracker.model.LocationListModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class MainPresenter: MainContract.Presenter {

    private val subscriptions = CompositeDisposable()
    private val api: ApiServiceInterface = ApiServiceInterface.create()
    private lateinit var view: MainContract.View

    override fun subscribe() {

    }

    override fun unsubscribe() {
        subscriptions.clear()
    }

    override fun attach(view: MainContract.View) {
        this.view = view
//        view.showListFragment() // as default
    }

    override fun loadLocations() {
        view.showProgress(true)
        var subscribe = api.getLocationList().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: LocationListModel? ->
                    view.loadDataSuccess(response!!.data)
                    view.showProgress(false)
                }, { error ->
                    view.showErrorMessage(error.localizedMessage)
                    view.showProgress(false)
                })

        subscriptions.add(subscribe)
    }

    override fun checkAvailability(startTime: Long, endTime: Long) {
        view.showProgress(true)
        var subscribe = api.getAvailabilityList(startTime, endTime).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: AvailabilityListModel? ->
                    view.loadAvailability(response!!.data)
                    view.showProgress(false)
                }, { error ->
                    view.showErrorMessage(error.localizedMessage)
                    view.showProgress(false)
                })

        subscriptions.add(subscribe)
    }
}