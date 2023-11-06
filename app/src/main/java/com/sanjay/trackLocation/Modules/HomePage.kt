package com.sanjay.trackLocation.Modules

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.sanjay.trackLocation.Adapter.LocationHistoryRVAdapter
import com.sanjay.trackLocation.Database.LocationObject
import com.sanjay.trackLocation.Database.OperationModel
import com.sanjay.trackLocation.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomePage : AppCompatActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var recyclerView: RecyclerView
    private lateinit var msgTV: TextView
    private lateinit var toolbar: Toolbar
    private lateinit var userTV: TextView
    private lateinit var handler: Handler

    private lateinit var operationModel: OperationModel

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    private val REQUEST_CODE = 10

    companion object {
        lateinit var locationsData: List<LocationObject>
        var currentIndex: Int = 0
        var loggedInUserID: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        toolbar = findViewById(R.id.toolbar)
        toolbar.title = "Home"
        setSupportActionBar(toolbar)

        userTV = findViewById(R.id.userTV)
        msgTV = findViewById(R.id.msg)

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        operationModel = ViewModelProvider(this)[OperationModel::class.java]
        val sharedPreferences: SharedPreferences =
            this.getSharedPreferences("data", Context.MODE_PRIVATE)
        userTV.text = sharedPreferences.getString("USERNAME", "U")?.get(0).toString()

//        getCurrentLocation()
        handler = Handler(Looper.getMainLooper())
        handler.postDelayed(runnable, 1000 * 30)

        recyclerView = findViewById(R.id.locationHistoryRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        getData()

        operationModel.locationResult.observe(this) {
            locationsData = it
            if (locationsData.size == 0) {
                recyclerView.visibility = View.GONE
                msgTV.visibility = View.VISIBLE
            } else {
                msgTV.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
                recyclerView.adapter =
                    LocationHistoryRVAdapter(this, locationsData) { selectedIndex ->
                        currentIndex = selectedIndex
                        val intent = Intent(this, ViewLocation::class.java)
                        startActivity(intent)
                    }
            }
        }

        userTV.setOnClickListener {
            val i = Intent(this, Profile::class.java)
            startActivity(i)
        }

    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentLocation() {
        if (checkPermission()) {
            if (isLocationEnable()) {
                fusedLocationProviderClient.lastLocation.addOnCompleteListener(this) {
                    val location: Location? = it.result
                    if (location != null) {
                        latitude = location.latitude
                        longitude = location.longitude
                        val geoCoder = Geocoder(this, Locale.getDefault())
                        val address: List<Address> =
                            geoCoder.getFromLocation(latitude, longitude, 1) as List<Address>
                        // val street = address[0].getAddressLine(0)
                        val city = address[0].locality
                        val state = address[0].adminArea
                        val country = address[0].countryName
                        val pinCode = address[0].postalCode
                        // val name = address[0].featureName

                        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm aaa")
                        val now = sdf.format(Date())

                        operationModel.writeLocation(
                            loggedInUserID,
                            city,
                            state,
                            pinCode,
                            country,
                            latitude,
                            longitude,
                            now
                        ).let {
                            getData()
                            Toast.makeText(this, "Location Updated", Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this, "Unable to get location", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermission()
        }
    }

    private fun checkPermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                )
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            REQUEST_CODE
        )
    }

    private fun isLocationEnable(): Boolean {
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    val runnable: Runnable = object : Runnable {
        override fun run() {
            getCurrentLocation()
            handler.postDelayed(this, 1000 * 60 * 15)
        }
    }

    private fun getData() {
        operationModel.getLocationFromDB(loggedInUserID)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}