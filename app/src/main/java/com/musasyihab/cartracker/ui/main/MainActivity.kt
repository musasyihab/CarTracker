package com.musasyihab.cartracker.ui.main

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import android.location.Criteria
import android.location.LocationManager
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ProgressBar
import android.widget.Toast
import com.musasyihab.cartracker.R
import com.musasyihab.cartracker.di.component.DaggerActivityComponent
import com.musasyihab.cartracker.di.module.ActivityModule
import com.musasyihab.cartracker.model.AvailabilityFormModel
import com.musasyihab.cartracker.model.AvailabilityModel
import com.musasyihab.cartracker.model.LocationModel
import com.musasyihab.cartracker.util.Constants
import com.musasyihab.cartracker.ui.view.AvailabilityFormView
import java.util.*
import javax.inject.Inject


class MainActivity : FragmentActivity(), OnMapReadyCallback, MainContract.View {

    @Inject
    lateinit var presenter: MainContract.Presenter

    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var mapView: FrameLayout? = null
    private var progressBar: ProgressBar? = null
    private var bookBtn: Button? = null
    private var showAllCarBtn: Button? = null
    private var formView: AvailabilityFormView? = null
    private var locations: List<LocationModel> = Collections.emptyList()
    private var availabilities: List<AvailabilityModel> = Collections.emptyList()
    private var isShowLocation: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependency()
        presenter.attach(this)
        presenter.subscribe()
        initView()
    }

    private fun initView() {
        mapView = findViewById(R.id.mapView) as FrameLayout
        formView = findViewById(R.id.formView) as AvailabilityFormView
        progressBar = findViewById(R.id.progressBar) as ProgressBar
        bookBtn = findViewById(R.id.bookBtn) as Button
        showAllCarBtn = findViewById(R.id.showAllCarBtn) as Button
        presenter.loadLocations()

        formView!!.setListener(object : AvailabilityFormView.OnFormSubmited {
            override fun onFormSubmited(form: AvailabilityFormModel) {
                presenter.checkAvailability(form.startTime, form.endTime)
            }
        })

        showAllCarBtn!!.setOnClickListener {
            loadDataSuccess(locations)
        }

    }

    private fun initMap() {
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment!!.getMapAsync(this)
    }

    override fun loadDataSuccess(list: List<LocationModel>) {
        isShowLocation = true
        locations = list;
        showAllCarBtn!!.visibility = View.GONE
        initMap()
    }

    override fun loadAvailability(list: List<AvailabilityModel>) {
        isShowLocation = false
        availabilities = list
        showAllCarBtn!!.visibility = View.VISIBLE
        initMap()
    }

    override fun showProgress(show: Boolean) {
        if(show){
            progressBar!!.visibility = View.VISIBLE
            mapView!!.visibility = View.GONE
            showAllCarBtn!!.visibility = View.GONE
        } else {
            progressBar!!.visibility = View.GONE
            if (locations.isNotEmpty()){
                mapView!!.visibility = View.VISIBLE
            }
        }
    }

    override fun showErrorMessage(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
    }

    private fun injectDependency() {
        val activityComponent = DaggerActivityComponent.builder()
                .activityModule(ActivityModule(this))
                .build()

        activityComponent.inject(this)
    }

    private fun getMyLocation(): Location? {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val defaultLocation = Location("defaultLocation")
        defaultLocation.latitude = Constants.DEFAULT_LOCATION.LATITUDE
        defaultLocation.longitude = Constants.DEFAULT_LOCATION.LONGITUDE
        if (checkLocationPermission()) {
            var myLocation: Location? = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (myLocation == null) {
                val criteria = Criteria()
                criteria.accuracy = Criteria.ACCURACY_COARSE
                val provider = lm.getBestProvider(criteria, true)
                myLocation = lm.getLastKnownLocation(provider)
            }
            return myLocation
        }
        return defaultLocation;
    }

    private fun isLocationPermissionGranted(): Boolean {
        return isAboveAndroidM() &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isAboveAndroidM(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    private fun checkLocationPermission(): Boolean {
        if(!isLocationPermissionGranted()) {
            if (isAboveAndroidM() && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), Constants.REQUEST_CODE_ASK_PERMISSIONS_LOCATION)
            }
        }
        return isLocationPermissionGranted()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            Constants.REQUEST_CODE_ASK_PERMISSIONS_LOCATION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                onMapReady(mMap)
            } else {
                // Permission Denied
                showErrorMessage(getString(R.string.access_location_denied))
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun loadMarkerToMap(mMap: GoogleMap?) {
        mMap!!.clear()
        if (isShowLocation) {
            if(locations.isEmpty()) {
                showErrorMessage(getString(R.string.no_location_found))
            }
            for (item in locations) {
                val latLng = LatLng(item.latitude, item.longitude)
                val status = if (item.is_on_trip) getString(R.string.car_on_trip) else getString(R.string.car_available)
                val icon = if (item.is_on_trip) R.drawable.car_icon_disabled else R.drawable.car_icon_available

                val title = getString(R.string.car_id) + item.id + " (" + status + ")"

                mMap.addMarker(
                        MarkerOptions().position(latLng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromResource(icon))
                )
            }
        } else {
            if(availabilities.isEmpty()) {
                showErrorMessage(getString(R.string.no_available_found))
            }
            for (item in availabilities) {
                for (dropoff in item.dropoff_locations) {
                    val latLng = LatLng(dropoff.location[0], dropoff.location[1])
                    val title = getString(R.string.possible_drop_off) + item.id
                    mMap.addMarker(
                            MarkerOptions().position(latLng)
                                    .title(title)
                    )
                }
                val latLng = LatLng(item.location[0], item.location[1])
                val title = getString(R.string.car_location) + item.id + " (" + item.available_cars + " " + getString(R.string.availables) + ")"
                mMap.addMarker(
                        MarkerOptions().position(latLng)
                                .title(title)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon_available))
                )
            }
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap?) {
        if(googleMap==null){
            return
        }
        val zoomLevel = 16.5.toFloat()
        mMap = googleMap

        var currentLocation: LatLng
        val myLocation = getMyLocation()
        currentLocation = LatLng(myLocation!!.latitude, myLocation!!.longitude)
        if(isAboveAndroidM() &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap!!.isMyLocationEnabled = true
        }
        loadMarkerToMap(mMap)

        mMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, zoomLevel))
    }
}
