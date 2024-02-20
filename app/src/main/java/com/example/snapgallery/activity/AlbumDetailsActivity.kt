package com.example.snapgallery.activity

import android.content.ContentUris
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.example.snapgallery.adapter.MediaAdapter
import com.example.snapgallery.model.MediaItem
import com.example.snapgallery.model.MediaType
import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView

class AlbumDetailsActivity : AppCompatActivity() {

    private lateinit var albumContentRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private var allMediaItems = listOf<MediaItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)

        val albumNameTextView: TextView = findViewById(R.id.albumNameTextView)
        albumContentRecyclerView = findViewById(R.id.albumContentRecyclerView)
        emptyTextView = findViewById(R.id.empty_text_view)

        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            onBackPressed() // Wywołaj metodę onBackPressed(), aby wrócić do poprzedniego widoku
        }

        val albumId = intent.getStringExtra("album_id")
        val albumName = intent.getStringExtra("album_name")
        albumNameTextView.text = albumName ?: "Brak nazwy albumu"

        configureRecyclerView()

        allMediaItems = fetchMedia(albumId ?: "")

        findViewById<Button>(R.id.showImagesButton).setOnClickListener {
            displayMediaType(MediaType.IMAGE)
        }

        findViewById<Button>(R.id.showVideosButton).setOnClickListener {
            displayMediaType(MediaType.VIDEO)
        }

        displayMediaType(MediaType.IMAGE)
    }

    private fun configureRecyclerView() {
        val orientation = resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        albumContentRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
    }

    private fun displayMediaType(mediaType: MediaType) {
        val filteredMediaItems = allMediaItems.filter { it.type == mediaType }
        albumContentRecyclerView.adapter = MediaAdapter(this, filteredMediaItems) { uris, position ->
            val intent = Intent(this, FullScreenPhotoAlbumActivity::class.java).apply {
                putParcelableArrayListExtra("albumImagesUris", uris)
                putExtra("selectedImageIndex", position)
            }
            startActivity(intent)
        }

        // Ustaw widoczność TextView w zależności od stanu listy mediów
        if (filteredMediaItems.isEmpty()) {
            emptyTextView.visibility = View.VISIBLE
        } else {
            emptyTextView.visibility = View.GONE
        }
    }


    private fun fetchMedia(albumId: String): List<MediaItem> {
        return fetchImages(albumId) + fetchVideos(albumId).map { uri ->
            MediaItem(0L, uri, MediaType.VIDEO)
        }
    }

    private fun fetchImages(albumId: String?): List<MediaItem> {
        val imageList = mutableListOf<MediaItem>()
        val projection = arrayOf(MediaStore.Images.Media._ID)

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            if (albumId != null) selection else null,
            if (albumId != null) selectionArgs else null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"

        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(MediaItem(id, contentUri, MediaType.IMAGE))
            }
        }

        return imageList
    }

    private fun fetchVideos(albumId: String): List<Uri> {
        val videoList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Video.Media._ID)
        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Video.Media.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                videoList.add(contentUri)
            }
        }

        return videoList
    }
}

