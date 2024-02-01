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
class AlbumAdapter(private var albums: List<Album>) : RecyclerView.Adapter<AlbumAdapter.AlbumViewHolder>() {
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
    }

    // Aktualizacja danych w adapterze
    fun updateData(newAlbums: List<Album>) {
        this.albums = newAlbums
        notifyDataSetChanged()
    }

    // Zwraca liczbę elementów w liście albumów
    override fun getItemCount() = albums.size
}
