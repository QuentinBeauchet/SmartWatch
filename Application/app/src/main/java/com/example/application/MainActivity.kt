package com.example.application

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.application.databinding.ActivityMainBinding
import com.example.application.databinding.MenuBinding
import org.json.JSONObject


class MainActivity : Activity() {

    lateinit var binding: ActivityMainBinding
    lateinit var menuBinding: MenuBinding
    private var positionX = 0.0f
    private var eventTypes = ArrayList<JSONObject>()

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val REQUEST_COARSE_AND_FINE_LOCATION_RESULT_CODE = 101
        private var DEVICE_NAME: String? = null
        private var DEVICE_ID: String? = null
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DEVICE_NAME = Settings.Global.getString(contentResolver, "device_name")
        DEVICE_ID = Settings.Secure.getString(contentResolver, "android_id")

        Log.d(LOG_TAG, BuildConfig.API_URL)

        checkPermissions()
        connectToAPI()
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

    private fun startLocalisationService(id: String) {
        Intent(this, UpdateLocation::class.java).also { intent ->
            Log.d(LOG_TAG, "STARTING SERVICE")
            intent.putExtra("USER_ID", id)
            startService(intent)
        }
    }

    private fun stopLocalisationService() {
        Intent(this, UpdateLocation::class.java).also { intent ->
            Log.d(LOG_TAG, "STOPPING SERVICE")
            stopService(intent)
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
                startLocalisationService(response.get("id").toString())
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
                menuBinding= MenuBinding.inflate(layoutInflater)
                val menu = Menu(menuBinding,eventTypes)
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