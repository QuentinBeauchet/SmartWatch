package com.example.application.fragments

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.application.BuildConfig
import com.example.application.R
import com.example.application.databinding.TypeBinding
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject

class TypesAdapter(private var types: JSONArray) :
    RecyclerView.Adapter<TypesAdapter.ItemsViewHolder>() {

    class ItemsViewHolder(val bindingMenu: TypeBinding) :
        RecyclerView.ViewHolder(bindingMenu.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: TypeBinding = TypeBinding.inflate(layoutInflater, parent, false)
        return ItemsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return types.length()
    }

    override fun onBindViewHolder(holder: ItemsViewHolder, position: Int) {
        val type: JSONObject = types.getJSONObject(position)
        holder.bindingMenu.icon.id = position
        holder.bindingMenu.icon.text = type.getString("name")

        setIcon(type.getString("icon"), holder)

    }

    private fun setIcon(path: String, holder: ItemsViewHolder) {
        val context = holder.itemView.context

        Picasso.get().load("${BuildConfig.API_URL}${path}")
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    holder.bindingMenu.icon.chipIcon =
                        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher, null)
                }
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    holder.bindingMenu.icon.chipIcon = BitmapDrawable(context.resources, bitmap)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    holder.bindingMenu.icon.chipIcon =
                        ResourcesCompat.getDrawable(context.resources, R.drawable.ic_launcher, null)
                }
            })

    }
}

