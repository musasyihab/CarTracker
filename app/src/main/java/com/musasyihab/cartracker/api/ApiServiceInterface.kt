package com.musasyihab.cartracker.api

import com.musasyihab.cartracker.model.AvailabilityListModel
import com.musasyihab.cartracker.model.LocationListModel
import com.musasyihab.cartracker.util.Constants
import io.reactivex.Observable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiServiceInterface {

    @GET("availability")
    fun getAvailabilityList(@Query(value="startTime") startTime: Long, @Query(value="endTime") endTime: Long): Observable<AvailabilityListModel>

    @GET("locations")
    fun getLocationList(): Observable<LocationListModel>

    companion object Factory {
        fun create(): ApiServiceInterface {
            val interceptor = HttpLoggingInterceptor()
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = retrofit2.Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(Constants.BASE_URL)
                    .client(client)
                    .build()

            return retrofit.create(ApiServiceInterface::class.java)
        }
    }
}