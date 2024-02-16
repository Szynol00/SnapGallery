package com.example.snapgallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgallery.R
import com.example.snapgallery.model.MediaItem
import com.example.snapgallery.model.MediaType

class MediaAdapter(private val mediaItems: List<MediaItem>, private val onMediaClick: (MediaItem) -> Unit) : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.media_item, parent, false)
        return MediaViewHolder(view, onMediaClick)
    }

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        val mediaItem = mediaItems[position]
        holder.bind(mediaItem)
    }

    override fun getItemCount() = mediaItems.size

    class MediaViewHolder(private val view: View, private val onMediaClick: (MediaItem) -> Unit) : RecyclerView.ViewHolder(view) {
        private val mediaImage: ImageView = view.findViewById(R.id.media_image)
        private val playIcon: ImageView = view.findViewById(R.id.play_icon)

        fun bind(mediaItem: MediaItem) {
            // Załaduj miniaturę mediów (zakładając, że używasz Glide)
            Glide.with(view.context)
                .load(mediaItem.uri)
                .into(mediaImage)

            // Ustaw widoczność ikony odtwarzania
            playIcon.visibility = if (mediaItem.type == MediaType.VIDEO) View.VISIBLE else View.GONE

            // Ustawienie listenera kliknięcia
            view.setOnClickListener {
                onMediaClick(mediaItem)
            }
        }
    }
}
