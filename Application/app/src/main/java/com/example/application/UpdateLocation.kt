package com.example.application

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateLocation : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var isServiceStarted = false

    companion object {
        private val LOG_TAG = UpdateLocation::class.java.simpleName
        private const val UPDATE_DELAY = 5000L
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Starting automatic updates", Toast.LENGTH_SHORT).show()
        if (!isServiceStarted) {
            isServiceStarted = true

            // we need this lock so our service gets not affected by Doze Mode
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                        acquire(60 * 60 * 1000L /*60 minutes*/)
                    }
                }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            GlobalScope.launch(Dispatchers.IO) {
                while (isServiceStarted) {
                    launch(Dispatchers.IO) {
                        updateLocation()
                    }
                    delay(UPDATE_DELAY)
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        Toast.makeText(this, "Stopping automatic updates", Toast.LENGTH_SHORT).show()
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        stopSelf()
        isServiceStarted = false
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        Log.d(LOG_TAG,"UPDATE")
        fusedLocationClient!!.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val location = task.result
                if (location != null){
                    Log.d(
                        LOG_TAG,
                        "Location :) -> {lat: ${location.latitude} long: ${location.longitude}}"
                    )
                }
            } else {
                Log.d(LOG_TAG, "get failed with ", task.exception)
            }
        }

        return
    }

}