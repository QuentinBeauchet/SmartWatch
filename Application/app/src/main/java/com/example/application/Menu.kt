package com.example.application

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.wear.widget.WearableLinearLayoutManager
import com.example.application.databinding.MenuBinding

class Menu(private val binding : MenuBinding) {
    private var icons = java.util.ArrayList<Item>()


    fun createListOfItems(context: Context) {
        binding.main.layoutManager = WearableLinearLayoutManager(context)
        binding.main.setHasFixedSize(true)
        binding.main.isEdgeItemsCenteringEnabled = true

        // Ajout des chips
        icons.add(
            Item(
                "Police",
                ContextCompat.getDrawable(context, R.drawable.ic_launcher)
            )
        )
        icons.add(
            Item(
                "Accident",
                ContextCompat.getDrawable(context, R.drawable.ic_launcher)
            )
        )
        // Lien entre WearableRecyclerView et RecyclerView grace a l'adapter
        val adapter = ItemsAdapter(icons)
        binding.main.adapter = adapter
    }
}