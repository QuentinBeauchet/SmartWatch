package com.example.application

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import com.example.application.databinding.ActivityMainBinding

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val REQUEST_COARSE_AND_FINE_LOCATION_RESULT_CODE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkPermissions()
        startLocalisationService()

    }

    override fun onDestroy() {
        super.onDestroy()
        stopLocalisationService()
    }

    private fun checkPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d(LOG_TAG, "All required location permission are already granted")
            //startLocationUpdates()
        } else {
            Log.d(LOG_TAG, "ask for coarse and fine location permission")
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ),
                REQUEST_COARSE_AND_FINE_LOCATION_RESULT_CODE
            )
        }
    }

    private fun startLocalisationService(){
        Intent(this, UpdateLocation::class.java).also { intent ->
            Log.d(LOG_TAG,"STARTING SERVICE")
            startService(intent)
        }
    }

    private fun stopLocalisationService(){
        Intent(this, UpdateLocation::class.java).also { intent ->
            Log.d(LOG_TAG,"STOPPING SERVICE")
            stopService(intent)
        }
    }

}