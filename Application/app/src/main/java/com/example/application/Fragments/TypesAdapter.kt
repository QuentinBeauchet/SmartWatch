package com.example.application.fragments

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
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
        holder.bindingMenu.type.id = position
        holder.bindingMenu.type.text = type.getString("name")

        setIcon(type.getString("icon"), holder)
        setListener(holder, type.getInt("id"))
    }

    private fun setIcon(path: String, holder: ItemsViewHolder) {
        val context = holder.itemView.context

        Picasso.get().load("${BuildConfig.API_URL}${path}")
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    holder.bindingMenu.type.chipIcon =
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.unknown,
                            null
                        )
                }

                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    holder.bindingMenu.type.chipIcon = BitmapDrawable(context.resources, bitmap)
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    holder.bindingMenu.type.chipIcon =
                        ResourcesCompat.getDrawable(
                            context.resources,
                            R.drawable.unknown,
                            null
                        )
                }
            })
    }

    private fun setListener(holder: ItemsViewHolder, id: Int) {
        val context = holder.itemView.context

        val input = EditText(context)
        input.inputType = InputType.TYPE_CLASS_TEXT

        val dialog = AlertDialog.Builder(context)
            .setTitle("Comment")
            .setView(input)
            .setPositiveButton("ok") { dialog, _ ->
                dialog.dismiss()
                if (StartupFragment.mService == null) {
                    Toast.makeText(
                        context,
                        "Location Service was not initialized yet", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    StartupFragment.mService?.postToApi(id, input.text.toString())
                }

            }
            .setNegativeButton("cancel") { dialog, _ -> dialog.cancel() }.create()

        holder.bindingMenu.type.setOnClickListener {
            dialog.show()
        }
    }
}

