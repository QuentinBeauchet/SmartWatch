package com.example.application.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.application.BuildConfig
import com.example.application.R
import com.example.application.location.LocationService
import com.google.android.material.slider.Slider

class ParametersFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_parameters, container, false)

        val slider = view.findViewById<Slider>(R.id.slider)
        val nbr = view.findViewById<TextView>(R.id.nbr)

        setText(nbr, BuildConfig.SLIDER_DEFAULT)

        slider.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            override fun onStartTrackingTouch(slider: Slider) {}

            override fun onStopTrackingTouch(slider: Slider) {
                val value = slider.value.toLong()
                setText(nbr, value)
                StartupFragment.mService?.setInterval(value)
            }
        })

        return view
    }

    private fun setText(view: TextView, value: Long) {
        val txt = "${value}s"
        view.text = txt
    }


    companion object {
        @JvmStatic
        fun newInstance() =
            ParametersFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}