package com.example.snapgallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.bumptech.glide.Glide

class ImageAdapter(
    private val images: List<Uri>,
    private val onImageClick: ((ArrayList<Uri>, Uri) -> Unit)? = null
) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_view) // Uwaga: poprawione id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = images[position]

        Glide.with(holder.imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_error)
            .into(holder.imageView)

        // Ustawienie onClickListener dla każdego elementu
        holder.imageView.setOnClickListener {
            onImageClick?.invoke(images as ArrayList<Uri>, imageUri)
        }
    }

    override fun getItemCount() = images.size
}
