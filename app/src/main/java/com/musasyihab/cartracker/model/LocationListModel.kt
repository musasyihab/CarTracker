package com.musasyihab.cartracker.model

import com.google.gson.Gson

data class LocationListModel(val data: List<LocationModel>) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}