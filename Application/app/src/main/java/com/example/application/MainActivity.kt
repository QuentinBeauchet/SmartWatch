package com.example.application

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.application.databinding.ActivityMainBinding
import com.example.application.databinding.MenuBinding
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var menuBinding: MenuBinding
    private var positionX = 0.0f
    private var eventTypes = ArrayList<JSONObject>()

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private lateinit var DEVICE_NAME: String
        private lateinit var DEVICE_ID: String
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DEVICE_NAME = Settings.Global.getString(contentResolver, "device_name")
        DEVICE_ID = Settings.Secure.getString(contentResolver, "android_id")

        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()
        connectToAPI()
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ),
            0
        )
    }

    private fun buildView(id: String) {
        findViewById<Button>(R.id.start).setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                putExtra("id", id)
                startService(this)
            }
        }

        findViewById<Button>(R.id.stop).setOnClickListener {
            Intent(applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                startService(this)
            }
        }
    }

    private fun connectToAPI() {
        val requestsQueue = Volley.newRequestQueue(this)
        val data = JSONObject()
        data.put("device_id", DEVICE_ID)
        data.put("name", DEVICE_NAME)

        val request = JsonObjectRequest(
            Request.Method.POST, "${BuildConfig.API_URL}/connect", data,
            { response ->
                Log.d(LOG_TAG, "Connexion successful from user $response")
                buildView(response.get("id").toString())
                getEventTypes(requestsQueue)
            }
        ) {
            run {
                Toast.makeText(
                    applicationContext,
                    "Connection Failed", Toast.LENGTH_SHORT
                ).show()
                Log.w(LOG_TAG, "The API is not available for connection")
                finish()
            }
        }

        requestsQueue.add(request)
    }

    private fun getEventTypes(requestsQueue: RequestQueue) {
        val request = JsonArrayRequest(
            Request.Method.GET, "${BuildConfig.API_URL}/types", null,
            { types ->
                Log.d(LOG_TAG, "The API has sent the event types successfully")

                for (i in 0 until types.length()) {
                    eventTypes.add(types.getJSONObject(i))
                }

                requestsQueue.stop()

                Log.d("Event", eventTypes.toString())
                menuBinding = MenuBinding.inflate(layoutInflater)
                val menu = Menu(menuBinding, eventTypes)
                menu.createListOfItems(this)

            }
        ) { Log.w(LOG_TAG, "The API is not available for types request") }

        requestsQueue.add(request)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val longOfDrag = 150
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                positionX = event.x
            }
            MotionEvent.ACTION_UP -> {
                if (positionX - event.x >= longOfDrag) {
                    Log.d("Swipe", "LEFT")
                    setContentView(menuBinding.root)
                }
            }
        }
        return true
    }
}