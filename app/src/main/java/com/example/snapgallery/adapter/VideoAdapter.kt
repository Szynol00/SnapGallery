package com.example.snapgallery.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.bumptech.glide.Glide
import com.example.snapgallery.VideoRepository
import com.example.snapgallery.activity.VideoPlayerActivity

class VideoAdapter(private val videos: List<Uri>, private val onVideoClick: (Uri) -> Unit) : RecyclerView.Adapter<VideoAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var videoView: ImageView = itemView.findViewById(R.id.video_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.video_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val videoUri = videos[position]

        // Load video thumbnail into ImageView
        Glide.with(holder.videoView.context)
            .load(videoUri)
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_error)
            .into(holder.videoView)

        // Set click listener for each video item
        holder.videoView.setOnClickListener {
            // Przykład wywołania metody z VideoRepository lub ViewModel
            // Zakładam, że VideoRepository może przechowywać wybrane indeksy i listę Uri
            VideoRepository.selectedVideoIndex = position
            VideoRepository.videos = videos

            val intent = Intent(holder.videoView.context, VideoPlayerActivity::class.java)
            // Możesz przekazać dodatkowe dane, jeśli są potrzebne, np. indeks wybranego filmu
            // Przykład przekazania indeksu; rzeczywista implementacja zależy od Twojej logiki
            intent.putExtra("selectedVideoIndex", position)
            holder.videoView.context.startActivity(intent)

            // Wywołanie callbacku, jeśli jest potrzebne
            onVideoClick(videoUri)
        }
    }


    override fun getItemCount() = videos.size
}
