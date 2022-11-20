package com.example.application

import android.content.Context
import androidx.wear.widget.WearableLinearLayoutManager
import com.example.application.databinding.MenuBinding
import org.json.JSONObject


class Menu(private val binding : MenuBinding,private var eventTypes : ArrayList<JSONObject>) {
    private var chips = java.util.ArrayList<Item>()


    fun createListOfItems(context: Context) {
        binding.main.layoutManager = WearableLinearLayoutManager(context)
        binding.main.setHasFixedSize(true)
        binding.main.isEdgeItemsCenteringEnabled = true

        // Ajout des chips
        for (i in 0 until eventTypes.size) {
            val id = eventTypes[i].getString("id")
            val text = eventTypes[i].getString("name")
            val icon = eventTypes[i].getString("icon")
            chips.add(Item(id.toInt(),text,"${BuildConfig.API_URL}${icon}"))

        }

        // Lien entre WearableRecyclerView et RecyclerView grace a l'adapter
        val adapter = ItemsAdapter(chips,context)
        binding.main.adapter = adapter
    }



}