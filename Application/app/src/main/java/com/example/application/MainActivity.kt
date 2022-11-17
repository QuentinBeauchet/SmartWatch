package com.example.application

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.application.databinding.ActivityMainBinding
import org.json.JSONObject

class MainActivity : Activity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private const val REQUEST_COARSE_AND_FINE_LOCATION_RESULT_CODE = 101
        private var DEVICE_NAME: String? = null
        private var DEVICE_ID: String? = null
        private const val API_URL = "http://172.30.160.1:3000/api/connect"
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        DEVICE_NAME = Settings.Global.getString(contentResolver, "device_name")
        DEVICE_ID = Settings.Secure.getString(contentResolver, "android_id")

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
            intent.putExtra("USER_ID",id)
            startService(intent)
        }
    }

    private fun stopLocalisationService() {
        Intent(this, UpdateLocation::class.java).also { intent ->
            Log.d(LOG_TAG, "STOPPING SERVICE")
            stopService(intent)
        }
    }

    private fun connectToAPI(){
        val requestsQueue = Volley.newRequestQueue(this)
        val data = JSONObject()
        data.put("device_id", DEVICE_ID)
        data.put("name", DEVICE_NAME)

        val request = JsonObjectRequest(
            Request.Method.POST, API_URL, data,
            {response ->
                Log.d(LOG_TAG, "Connexion response: $response")
                startLocalisationService(response.get("id").toString())
                requestsQueue.stop()
            }
        ) { error -> error.printStackTrace() }

        requestsQueue.add(request)
    }

}