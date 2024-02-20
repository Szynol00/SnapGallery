package com.example.snapgallery.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgallery.R

class FullScreenPhotoAlbumAdapter(
    private val context: Context,
    private val imagesUris: ArrayList<Uri>
) : RecyclerView.Adapter<FullScreenPhotoAlbumAdapter.ImageViewHolder>() { // Zmiana tutaj

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.fullscreen_image_item, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val uri = imagesUris[position]
        Glide.with(context).load(uri).into(holder.imageView)
    }

    override fun getItemCount(): Int = imagesUris.size

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.fullscreenImageView)
    }
}
