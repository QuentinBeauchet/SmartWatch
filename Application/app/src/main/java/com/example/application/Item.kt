package com.example.application


import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.application.databinding.MenuRecyclerBinding
import com.squareup.picasso.Picasso


class Item(var id: Int, var text: String, var url: String) {


    class ItemsAdapter(private var icons: ArrayList<Item>, var context: Context) :
        RecyclerView.Adapter<ItemsViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding: MenuRecyclerBinding =
                MenuRecyclerBinding.inflate(layoutInflater, parent, false)
            return ItemsViewHolder(binding)
        }

        override fun getItemCount(): Int {
            return icons.size
        }

        override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
            val icon = icons[position]
            holder.bindingMenu.icon.id = icon.id
            holder.bindingMenu.icon.text = icon.text


            Picasso.get().load(icon.url).into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    val drawable: Drawable? = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_launcher_foreground,
                        null
                    )
                    holder.bindingMenu.icon.chipIcon = drawable
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    // loaded bitmap is here (bitmap)
                    val d: Drawable = BitmapDrawable(context.resources, bitmap)
                    holder.bindingMenu.icon.chipIcon = d
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    val drawable: Drawable? = ResourcesCompat.getDrawable(
                        context.resources,
                        R.drawable.ic_launcher,
                        null
                    )
                    holder.bindingMenu.icon.chipIcon = drawable
                }

            })

        }
    }

    class ItemsViewHolder(val bindingMenu: MenuRecyclerBinding) :
        RecyclerView.ViewHolder(bindingMenu.root)

}