package com.example.snapgallery.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.bumptech.glide.Glide

class VideoAdapter(private val videos: List<Uri>) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoView: ImageView = itemView.findViewById(R.id.video_view)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoUri = videos[position]

        // Załaduj miniaturę wideo do ImageView
        Glide.with(holder.videoView.context)
            .load(videoUri)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_error)
            .into(holder.videoView)
    }

    override fun getItemCount() = videos.size
}
