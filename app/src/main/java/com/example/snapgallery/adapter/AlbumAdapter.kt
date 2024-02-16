package com.example.snapgallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapgallery.R
import com.example.snapgallery.model.Album
class AlbumAdapter(private var albums: List<Album>, private val listener: OnAlbumClickListener) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {

    interface OnAlbumClickListener {
        fun onAlbumClick(album: Album)
    }

    private var allAlbums: List<Album> = albums
    // Klasa ViewHolder dla każdego elementu albumu w RecyclerView

    class AlbumViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val thumbnailImageView: ImageView = view.findViewById(R.id.album_thumbnail)
        val albumNameTextView: TextView = view.findViewById(R.id.album_title)
        val albumPhotoCountTextView: TextView = view.findViewById(R.id.album_media_count)
    }
    // Tworzenie nowego ViewHoldera
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.album_item, parent, false)
        return AlbumViewHolder(view)
    }
    // Łączenie danych z widokiem dla każdego elementu
    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        val album = albums[position]
        val totalMediaCount = album.photoCount + album.videoCount
        holder.albumNameTextView.text = album.name
        val mediaCountText = "$totalMediaCount plików"
        holder.albumPhotoCountTextView.text = mediaCountText

        // Ładowanie miniatury albumu
        album.thumbnailUri?.let {
            Glide.with(holder.thumbnailImageView.context)
                .load(it)
                .thumbnail(0.25f) // Load a small thumbnail before the full image.
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_error)
                .into(holder.thumbnailImageView)
        }

        holder.itemView.setOnClickListener {
            listener.onAlbumClick(album)
        }
    }

    fun filter(query: String) {
        val lowerCaseQuery = query.lowercase()
        albums = allAlbums.filter {
            it.name.lowercase().contains(lowerCaseQuery)
        }
        notifyDataSetChanged()
    }

    // Aktualizacja danych w adapterze
    fun updateData(newAlbums: List<Album>) {
        this.allAlbums = newAlbums
        this.albums = newAlbums // Zaktualizuj albums tak, aby mógł być filtrowany
        notifyDataSetChanged()
    }

    // Zwraca liczbę elementów w liście albumów
    override fun getItemCount() = albums.size
}
