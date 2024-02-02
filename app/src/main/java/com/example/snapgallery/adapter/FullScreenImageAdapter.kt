package com.example.snapgallery.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.example.snapgallery.R

class FullScreenImageAdapter(private val context: Context, private val images: List<Uri>)
    : RecyclerView.Adapter<FullScreenImageAdapter.FullScreenImageViewHolder>() {

    inner class FullScreenImageViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FullScreenImageViewHolder {
        val imageView = LayoutInflater.from(context).inflate(R.layout.fullscreen_image_item, parent, false) as ImageView
        return FullScreenImageViewHolder(imageView)
    }


    override fun onBindViewHolder(holder: FullScreenImageViewHolder, position: Int) {
        val imageUri = images[position]
        Glide.with(context)
            .load(imageUri)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }
}

