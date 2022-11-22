package com.example.application

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
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
        //val view : ChipGroup? = activity?.findViewById(R.id.types);
        val view = inflater.inflate(R.layout.fragment_types, container, false)

        val group = view.findViewById<ChipGroup>(R.id.types)

        val jsonArray = JSONArray(types)
        for (i in 0 until jsonArray.length()) {
            val type = jsonArray.getJSONObject(i)
            val chip = inflater.inflate(R.layout.type, container, false)
            val item = chip.findViewById<Chip>(R.id.chip)
            item.text = type.getString("name")

            group.addView(chip)
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