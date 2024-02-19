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

class MediaAdapter(private var allMediaItems: List<MediaItem>, private val onClick: (MediaItem) -> Unit) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (allMediaItems[position].type) {
            MediaType.IMAGE -> VIEW_TYPE_IMAGE
            MediaType.VIDEO -> VIEW_TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> ImageViewHolder(layoutInflater.inflate(R.layout.image_item, parent, false), onClick)
            VIEW_TYPE_VIDEO -> VideoViewHolder(layoutInflater.inflate(R.layout.video_item, parent, false), onClick)
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val mediaItem = allMediaItems[position]
        when (holder) {
            is ImageViewHolder -> holder.bind(mediaItem)
            is VideoViewHolder -> holder.bind(mediaItem)
        }
    }

    override fun getItemCount(): Int = allMediaItems.size

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
    }

    class ImageViewHolder(itemView: View, private val onClick: (MediaItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(mediaItem: MediaItem) {
            if (mediaItem.uri != null) {
                Glide.with(itemView.context)
                    .load(mediaItem.uri)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .into(imageView)
            } else {
                // Jeśli nie ma URI, wyświetl placeholder lub informację o braku zdjęć
                imageView.setImageResource(R.drawable.image_placeholder)
            }

            itemView.setOnClickListener { onClick(mediaItem) }
        }
    }

    class VideoViewHolder(itemView: View, private val onClick: (MediaItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_view)

        fun bind(mediaItem: MediaItem) {
            if (mediaItem.uri != null) {
                Glide.with(itemView.context)
                    .load(mediaItem.uri)
                    .placeholder(R.drawable.image_placeholder)
                    .error(R.drawable.image_error)
                    .into(imageView)
            } else {
                // Jeśli nie ma URI, wyświetl placeholder lub informację o braku filmów

            }

            itemView.setOnClickListener { onClick(mediaItem) }
        }
    }
}
