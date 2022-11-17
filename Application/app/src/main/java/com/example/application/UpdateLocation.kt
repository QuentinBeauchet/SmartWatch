package com.example.application

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.*


class UpdateLocation : Service() {

    private var wakeLock: PowerManager.WakeLock? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var isServiceStarted = false
    private var requestsQueue: RequestQueue? = null

    companion object {
        private val LOG_TAG = UpdateLocation::class.java.simpleName
        private const val UPDATE_DELAY = 10000L
        private const val EVENT_TYPE = 0
        private const val API_URL = "http://172.30.160.1:3000/api/events/add"
        private var USER_ID : String? = null
    }

    @SuppressLint("HardwareIds")
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Toast.makeText(this, "Starting automatic updates", Toast.LENGTH_SHORT).show()
        if (!isServiceStarted) {
            isServiceStarted = true

            USER_ID = intent.getStringExtra("USER_ID")

            // we need this lock so our service gets not affected by Doze Mode
            wakeLock =
                (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
                    newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "EndlessService::lock").apply {
                        acquire(60 * 60 * 1000L /*60 minutes*/)
                    }
                }
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

            requestsQueue = Volley.newRequestQueue(this)

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
        isServiceStarted = false
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
        requestsQueue?.stop()
        stopSelf()
    }

    @SuppressLint("MissingPermission")
    private fun updateLocation() {
        fusedLocationClient!!.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val location = task.result
                    if (location != null) postToAPI(location.latitude, location.longitude)
                } else {
                    Log.d(LOG_TAG, "get failed with ", task.exception)
                }
            }

        return
    }

    private fun postToAPI(latitude: Double, longitude: Double) {
        if(requestsQueue != null){
            val data = JSONObject()
            data.put("user_id", USER_ID)
            data.put("type_id", EVENT_TYPE)
            data.put("latitude", latitude)
            data.put("longitude", longitude)
            data.put("comment","Test Comment from Android Studio")

            val request = JsonObjectRequest(
                Request.Method.POST, API_URL, data,
                {response ->
                    Log.d(LOG_TAG, "Location update status: $response")
                }
            ) { error -> error.printStackTrace() }

            requestsQueue?.add(request)
        }
    }

}