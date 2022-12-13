package com.example.application.fragments

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.application.location.LocationService
import com.example.application.R
import pl.droidsonroids.gif.GifImageView

class StartupFragment : Fragment() {
    private var userId: String? = null
    private var mBound: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(userId)
        }
    }

    override fun onResume() {
        super.onResume()

        if (working) {
            activity?.findViewById<GifImageView>(R.id.gps_off)?.visibility = View.GONE
            activity?.findViewById<GifImageView>(R.id.gps_on)?.visibility = View.VISIBLE
        }
    }

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as LocationService.LocationBinder
            mService = binder.getService()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_startup, container, false)
        val on = view.findViewById<GifImageView>(R.id.gps_on)
        val off = view.findViewById<GifImageView>(R.id.gps_off)

        view.findViewById<Button>(R.id.start).setOnClickListener {
            off.visibility = View.GONE
            on.visibility = View.VISIBLE
            working = true
            Intent(activity?.applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                putExtra("id", userId)
                activity?.startService(this)
                activity?.bindService(this, connection, Context.BIND_AUTO_CREATE)
            }

        }

        view.findViewById<Button>(R.id.stop).setOnClickListener {
            on.visibility = View.GONE
            off.visibility = View.VISIBLE
            working = false
            Intent(activity?.applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                putExtra("id", userId)
                activity?.startService(this)
                activity?.bindService(this, connection, Context.BIND_AUTO_CREATE)
            }
        }

        return view
    }

    override fun onStop() {
        super.onStop()
        if(mBound) activity?.unbindService(connection)
        mBound = false
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            StartupFragment().apply {
                arguments = Bundle().apply {
                    putString(userId, param1)
                }
            }

        private var working = false
        var mService: LocationService? = null
    }
}
