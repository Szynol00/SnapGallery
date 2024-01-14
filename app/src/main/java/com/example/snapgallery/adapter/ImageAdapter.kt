package com.example.snapgallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.example.snapgallery.model.Image
import com.bumptech.glide.Glide

class ImageAdapter(private val images: List<Uri>) : RecyclerView.Adapter<ImageAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.image_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val imageUri = images[position]

//         Załaduj zdjęcie do ImageView z obsługą placeholderów i błędów
        Glide.with(holder.imageView.context)
            .load(imageUri)
            .placeholder(R.drawable.image_placeholder) // Dodaj plik graficzny do projektu jako placeholder
            .error(R.drawable.image_error)             // Dodaj plik graficzny do projektu na wypadek błędu
            .into(holder.imageView)
    }

    override fun getItemCount() = images.size
}