package com.example.application

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import com.example.application.databinding.MenuBinding
import org.json.JSONObject

class Menu(private val binding : MenuBinding,private var eventTypes : ArrayList<JSONObject>) {
    private var chips = java.util.ArrayList<Item>()


    fun createListOfItems(context: Context) {

        // Ajout des chips
        for (i in 0 until eventTypes.size) {
            val id = eventTypes[i].getString("id")
            val text = eventTypes[i].getString("name")
            val icon = eventTypes[i].getString("icon")
            chips.add(Item(id.toInt(),text,"${BuildConfig.API_URL}${icon}"))

        }

        // Lien entre WearableRecyclerView et RecyclerView grace a l'adapter
        val adapter = Item.ItemsAdapter(chips, context)
        binding.main.isEdgeItemsCenteringEnabled = true
        binding.main.layoutManager = WearableLinearLayoutManager(context)
        binding.main.isCircularScrollingGestureEnabled = true
        binding.main.isEdgeItemsCenteringEnabled = true
        binding.main.layoutManager = WearableLinearLayoutManager(context,CustomScrollingLayoutCallback())
        binding.main.adapter = adapter
    }





    class CustomScrollingLayoutCallback : WearableLinearLayoutManager.LayoutCallback() {
        private val MAX_ICON_PROGRESS = 0.65f
        private var progressToCenter: Float = 0f

        override fun onLayoutFinished(child: View, parent: RecyclerView) {
            child.apply {
                // Figure out % progress from top to bottom
                val centerOffset = height.toFloat() / 2.0f / parent.height.toFloat()
                val yRelativeToCenterOffset = y / parent.height + centerOffset

                // Normalize for center
                progressToCenter = Math.abs(0.5f - yRelativeToCenterOffset)
                // Adjust to the maximum scale
                progressToCenter = Math.min(progressToCenter, MAX_ICON_PROGRESS)

                scaleX = 1 - progressToCenter
                scaleY = 1 - progressToCenter
            }
        }
    }



}