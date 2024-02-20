package com.example.snapgallery.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgallery.R
import com.example.snapgallery.activity.FullScreenPhotoAlbumActivity
import com.example.snapgallery.activity.VideoPlayerAlbumActivity
import com.example.snapgallery.model.MediaItem
import com.example.snapgallery.model.MediaType

class MediaAdapter(
    private val context: Context,
    private var allMediaItems: List<MediaItem>,
    private val onClick: ((ArrayList<Uri>, Int) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return when (allMediaItems[position].type) {
            MediaType.IMAGE -> VIEW_TYPE_IMAGE
            MediaType.VIDEO -> VIEW_TYPE_VIDEO
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_TYPE_IMAGE -> ImageViewHolder(layoutInflater.inflate(R.layout.image_item, parent, false))
            VIEW_TYPE_VIDEO -> VideoViewHolder(layoutInflater.inflate(R.layout.video_item, parent, false))
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> holder.bind(context, allMediaItems, position)
            is VideoViewHolder -> holder.bind(context, allMediaItems, position) // Zmieniono na przekazanie całej listy i pozycji
        }
    }


    override fun getItemCount(): Int = allMediaItems.size

    companion object {
        const val VIEW_TYPE_IMAGE = 1
        const val VIEW_TYPE_VIDEO = 2
    }

    class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.image_view)

        fun bind(context: Context, allMediaItems: List<MediaItem>, position: Int) {
            val mediaItem = allMediaItems[position]
            Glide.with(itemView.context)
                .load(mediaItem.uri)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into(imageView)

            itemView.setOnClickListener {
                val intent = Intent(context, FullScreenPhotoAlbumActivity::class.java).apply {
                    val imageUris = allMediaItems.filter { it.type == MediaType.IMAGE }.map { it.uri } as ArrayList
                    putParcelableArrayListExtra("albumImagesUris", imageUris)
                    putExtra("selectedImageIndex", imageUris.indexOf(mediaItem.uri))
                }
                context.startActivity(intent)
            }
        }
    }

    class VideoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.video_view)

        fun bind(context: Context, allMediaItems: List<MediaItem>, position: Int) {
            val mediaItem = allMediaItems[position]

            Glide.with(itemView.context)
                .load(mediaItem.uri)
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into(imageView)

            itemView.setOnClickListener {
                // Filtruj tylko filmy
                val videoUris = allMediaItems.filter { it.type == MediaType.VIDEO }.map { it.uri } as ArrayList<Uri>

                val intent = Intent(context, VideoPlayerAlbumActivity::class.java).apply {
                    // Przekazanie listy Uri filmów
                    putParcelableArrayListExtra("albumVideosUri", videoUris)
                    // Przekazanie indeksu wybranego filmu
                    putExtra("selectedVideoIndex", videoUris.indexOf(mediaItem.uri))
                }
                context.startActivity(intent)
            }
        }
    }



}
