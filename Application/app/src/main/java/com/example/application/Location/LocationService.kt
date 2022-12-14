package com.example.application.location

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.application.BuildConfig
import com.example.application.R
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject

class LocationService : Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val binder = LocationBinder()
    private lateinit var notificationManager: NotificationManager
    private lateinit var locationClient: LocationClient
    private lateinit var requestsQueue: RequestQueue

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
        private val LOG_TAG = LocationService::class.java.simpleName
        private const val EVENT_TYPE = 0
        private lateinit var USER_ID: String
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        locationClient = LocationClient(
            applicationContext,
            LocationServices.getFusedLocationProviderClient(applicationContext)
        )
        requestsQueue = Volley.newRequestQueue(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        USER_ID = intent?.getStringExtra("id")!!
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        Log.d(LOG_TAG, "Starting the location service")
        val notification = NotificationCompat.Builder(this, "location")
            .setContentTitle("Tracking location...")
            .setContentText("Location enabled")
            .setSmallIcon(R.drawable.ic_launcher)
            .setOngoing(true)

        locationClient
            .getLocationUpdates(BuildConfig.SLIDER_DEFAULT)
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                postToAPI(location.latitude, location.longitude, EVENT_TYPE, null)
            }
            .launchIn(serviceScope)

        startForeground(1, notification.build())
    }

    private fun stop() {
        Log.d(LOG_TAG, "Stopping the location service")
        stopForeground(STOP_FOREGROUND_DETACH)
        notificationManager.cancelAll()
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    fun setInterval(interval : Long){
        locationClient.setInterval(interval)
    }


    private fun postToAPI(latitude: Double, longitude: Double, type_id: Int, comment: String?) {
        val data = JSONObject()
        data.put("user_id", USER_ID)
        data.put("type_id", type_id)
        data.put("latitude", latitude)
        data.put("longitude", longitude)
        data.put("comment", comment)

        val request = JsonObjectRequest(
            Request.Method.POST, "${BuildConfig.API_URL}/events/add", data,
            { response ->
                Log.d(
                    LOG_TAG,
                    "Location update status was ${if (response.get("success") as Boolean) "successfully sent" else "malformed"}"
                )
            }
        ) { Log.w(LOG_TAG, "The API is not available for location update") }

        requestsQueue.add(request)
    }

    fun postToApi(id: Int, comment: String) {
        locationClient
            .makeLocationRequest()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                postToAPI(location.latitude, location.longitude, id, comment)
            }
            .launchIn(serviceScope)
    }

    class LocationApp : Application() {
        override fun onCreate() {
            super.onCreate()
            val channel = NotificationChannel(
                "location",
                "Location",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    inner class LocationBinder : Binder() {
        fun getService(): LocationService = this@LocationService
    }

}

