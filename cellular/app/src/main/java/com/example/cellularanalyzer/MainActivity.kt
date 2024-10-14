package com.example.cellularanalyzer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

const val PERMISSION_REQUEST_CAMERA = 0

@Suppress("UNREACHABLE_CODE")
class MainActivity : AppCompatActivity() {
    val value: Int = 0
    val LOG_TAG: String = "myLogs"

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }

    private lateinit var myFusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var tvLat: TextView
    private lateinit var tvLon: TextView
    private lateinit var updateLocationButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(LOG_TAG, "onCreate")
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val helloWorldView: TextView = findViewById(R.id.helloWorldTextView)
        helloWorldView.text = "onCreate hello"
        Log.d(LOG_TAG, helloWorldView.text.toString())

        myFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        tvLat = findViewById(R.id.lat)
        tvLon = findViewById(R.id.lon)
        updateLocationButton = findViewById(R.id.updateLocationButton)

        getCurrentLocation()

        updateLocationButton.setOnClickListener {
            getCurrentLocation()
        }
    }

    private fun getCurrentLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions()
                    return
                }
                myFusedLocationProviderClient.lastLocation.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        Toast.makeText(applicationContext, "Problems with signal", Toast.LENGTH_SHORT).show()
                    } else {
                        tvLat.text = location.latitude.toString()
                        tvLon.text = location.longitude.toString()
                    }
                }
            } else {
                // Открыть настройки для включения геолокации
                Toast.makeText(applicationContext, "Enable location in settings", Toast.LENGTH_SHORT).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            Log.w(LOG_TAG, "location permission is not allowed")
            tvLat.text = "Permission is not granted"
            tvLon.text = "Permission is not granted"
            requestPermissions()
        }
    }

    private fun requestPermissions() {
        Log.w(LOG_TAG, "requestPermissions()")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }
    private fun checkPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(applicationContext, "Denied by user", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(LOG_TAG, "onResume")
        val helloWorldView: TextView = findViewById(R.id.helloWorldTextView)
        helloWorldView.text = "Hello World"
        Log.d(LOG_TAG, helloWorldView.text.toString())
    }
}