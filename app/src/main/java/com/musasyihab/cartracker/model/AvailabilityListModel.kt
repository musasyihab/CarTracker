package com.musasyihab.cartracker.model

import com.google.gson.Gson

data class AvailabilityListModel(val data: List<AvailabilityModel>) {
    fun toJson(): String {
        return Gson().toJson(this)
    }
}