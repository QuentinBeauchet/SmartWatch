package com.example.application

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.widget.ViewPager2
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.application.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject


class MainActivity : FragmentActivity() {
    private lateinit var binding: ActivityMainBinding

    companion object {
        private val LOG_TAG = MainActivity::class.java.simpleName
        private lateinit var DEVICE_NAME: String
        private lateinit var DEVICE_ID: String
        private lateinit var USER_ID: String
    }

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DEVICE_NAME = Settings.Global.getString(contentResolver, "device_name")
        DEVICE_ID = Settings.Secure.getString(contentResolver, "android_id")

        binding = ActivityMainBinding.inflate(layoutInflater)
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

    private fun connectToAPI() {
        val requestsQueue = Volley.newRequestQueue(this)
        val data = JSONObject()
        data.put("device_id", DEVICE_ID)
        data.put("name", DEVICE_NAME)

        val request = JsonObjectRequest(
            Request.Method.POST, "${BuildConfig.API_URL}/connect", data,
            { response ->
                Log.d(LOG_TAG, "Connexion successful from user $response")
                USER_ID = response.get("id").toString()
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
                requestsQueue.stop()
                Log.d(LOG_TAG, "The API has sent the event types successfully")
                hideProgressBar(types)
            }
        ) { Log.w(LOG_TAG, "The API is not available for types request") }

        requestsQueue.add(request)
    }

    private fun hideProgressBar(types: JSONArray) {
        findViewById<ProgressBar>(R.id.progressbar).visibility = View.GONE

        val viewPager2: ViewPager2 = findViewById(R.id.viewpager)
        viewPager2.visibility = View.VISIBLE
        viewPager2.adapter =
            FragmentAdapter(supportFragmentManager, lifecycle, USER_ID, types.toString())
    }
}