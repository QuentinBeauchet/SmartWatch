package com.example.application

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class StartupFragment : Fragment() {
    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(userId)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_startup, container, false)

        view.findViewById<Button>(R.id.start).setOnClickListener {
            Intent(activity?.applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_START
                putExtra("id", userId)
                activity?.startService(this)
            }
        }

        view.findViewById<Button>(R.id.stop).setOnClickListener {
            Intent(activity?.applicationContext, LocationService::class.java).apply {
                action = LocationService.ACTION_STOP
                putExtra("id", userId)
                activity?.startService(this)
            }
        }

        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            StartupFragment().apply {
                arguments = Bundle().apply {
                    putString(userId, param1)
                }
            }
    }
}