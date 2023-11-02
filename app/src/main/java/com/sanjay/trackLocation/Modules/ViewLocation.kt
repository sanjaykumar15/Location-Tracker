package com.sanjay.trackLocation.Modules

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.sanjay.trackLocation.Database.LocationObject
import com.sanjay.trackLocation.R

class ViewLocation : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var toolbar: Toolbar
    private lateinit var userTV: TextView
    private lateinit var mapFragment: SupportMapFragment
    private lateinit var before: ImageView
    private lateinit var next: ImageView
    private lateinit var dateTimeTv: TextView

    private lateinit var myGoogleMap: GoogleMap

    private lateinit var locationsData: List<LocationObject>
    private var currentIndex: Int = 0
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_location)

        initiate()
        toolbar.title = "View Your Locations"
        setSupportActionBar(toolbar)

        userTV = findViewById(R.id.userTV)
        userTV.visibility = View.GONE

        locationsData = HomePage.locationsData
        currentIndex = HomePage.currentIndex

        mapFragment =
            supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if (currentIndex == 0){
            before.visibility = View.GONE
        }

        if (currentIndex == locationsData.size - 1){
            next.visibility = View.GONE
        }

        before.setOnClickListener {
            currentIndex -= 1
            if (currentIndex == 0) {
                before.visibility = View.GONE
            } else {
                before.visibility = View.VISIBLE
            }
            viewLocation()
        }

        next.setOnClickListener {
            currentIndex += 1
            if (currentIndex == locationsData.size - 1) {
                next.visibility = View.GONE
            } else {
                next.visibility = View.VISIBLE
            }
            viewLocation()
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        myGoogleMap = googleMap
        viewLocation()
    }

    private fun viewLocation() {
        latitude = locationsData[currentIndex].latitude
        longitude = locationsData[currentIndex].longitude
        val latLng = LatLng(latitude, longitude)
        myGoogleMap.addMarker(
            MarkerOptions().position(latLng).title(
                locationsData[currentIndex].city + ", " + locationsData[currentIndex].state
            )
        )
        myGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f))
        dateTimeTv.text = locationsData[currentIndex].dateTime
    }

    fun initiate() {
        toolbar = findViewById(R.id.toolbar)
        before = findViewById(R.id.navigateBefore)
        next = findViewById(R.id.navigateNext)
        dateTimeTv = findViewById(R.id.dateTimeTV)
    }

}