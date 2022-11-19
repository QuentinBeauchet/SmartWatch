package com.example.application

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.MenuBinding

class Item(var text : String,var img : Drawable?){

}




class ItemsAdapter(var icons : ArrayList<Item>) : RecyclerView.Adapter<ItemsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: MenuBinding = MenuBinding.inflate(layoutInflater, parent, false)
        return ItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return icons.size
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val element = icons[position]
        holder.bindingMenu.icon.text = element.text
        holder.bindingMenu.icon.chipIcon = element.img
    }
}

class ItemsViewHolder(val bindingMenu: MenuBinding) : RecyclerView.ViewHolder(bindingMenu.root){



}