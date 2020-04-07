package com.mariussd.triptrack

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import java.util.jar.Manifest

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    val PERMISSION_ID = 42
    lateinit var mFusedLocationProviderClient: FusedLocationProviderClient

    var latLong: LatLng = LatLng(0.0, 0.0)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        requestNewLocationData()
        getLastLocation()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val dritt = Location("gps")

        val x: Int = 5
        when(x) {
            5 -> println("hello")
            6 -> print ("bing bong")
        }

        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)

        var lat = dritt.latitude
        var long = dritt.longitude

        var pos = LatLng(lat, long)

        // mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
    }

    private fun getLastLocation() {
        if (checkPermission()) {
            if (isLocationEnabled()) {
                mFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    var location: Location? = task.result

                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        updateLocation(location.latitude, location.longitude)
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLong, 10F))
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

    private fun updateLocation(latitude: Double, longitude: Double) {
        latLong = LatLng(latitude, longitude)
        mMap.addCircle(CircleOptions().center(latLong).radius(1.5))
        mMap.addMarker(MarkerOptions().position(latLong).title("Yeet"))
        mMap.uiSettings.isMyLocationButtonEnabled = true
    }


    private fun requestNewLocationData() {
        var mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 5000
        mLocationRequest.fastestInterval = 500
        // mLocationRequest.numUpdates = 1

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult?) {
            super.onLocationResult(p0)
            if (p0 != null) {
                val mLastLocation: Location = p0.lastLocation
                updateLocation(mLastLocation.latitude, mLastLocation.longitude)
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_ID)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // TODO granted
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
