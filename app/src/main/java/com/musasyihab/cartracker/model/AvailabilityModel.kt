package com.musasyihab.cartracker.model

data class AvailabilityModel(val id: Int, val location: DoubleArray, val available_cars: Int, val dropoff_locations: List<DropoffLocationsModel>)