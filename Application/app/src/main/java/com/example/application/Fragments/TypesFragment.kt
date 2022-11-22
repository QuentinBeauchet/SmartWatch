package com.example.application.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.wear.widget.WearableRecyclerView
import com.example.application.R
import org.json.JSONArray


class TypesFragment : Fragment() {
    private var types: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            types = it.getString(types)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_types, container, false)

        view.findViewById<WearableRecyclerView>(R.id.types).apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = TypesAdapter(JSONArray(types))
        }

        return view
    }


    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            TypesFragment().apply {
                arguments = Bundle().apply {
                    putString(types, param1)
                }
            }
    }
}