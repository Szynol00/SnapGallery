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
import android.content.res.Configuration
import android.net.Uri
import android.view.View
import android.widget.Button
import android.widget.ImageView
import com.example.snapgallery.adapter.ImageAdapter
import com.example.snapgallery.adapter.VideoAdapter

class AlbumDetailsActivity : AppCompatActivity() {

    private lateinit var albumContentRecyclerView: RecyclerView
    private lateinit var emptyTextView: TextView
    private var images = listOf<Uri>()
    private var videos = listOf<Uri>()

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

        images = fetchImages(albumId ?: "")
        videos = fetchVideos(albumId ?: "")

        findViewById<Button>(R.id.showImagesButton).setOnClickListener {
            displayImages()
        }

        findViewById<Button>(R.id.showVideosButton).setOnClickListener {
            displayVideos()
        }

        displayImages()
    }

    private fun displayImages() {
        albumContentRecyclerView.adapter = ImageAdapter(images) { uris, imageUri ->
            val intent = Intent(this, FullScreenImageActivity::class.java).apply {
                val selectedImageIndex = uris.indexOf(imageUri)
                putParcelableArrayListExtra("albumImagesUris", uris)
                putExtra("selectedImageIndex", selectedImageIndex)
            }
            startActivity(intent)
        }

        // Ustaw widoczność TextView w zależności od stanu listy mediów
        if (images.isEmpty()) emptyTextView.visibility = View.VISIBLE
        else emptyTextView.visibility = View.GONE
    }

    private fun displayVideos() {
        albumContentRecyclerView.adapter = VideoAdapter(videos) { videoUri ->
            val intent = Intent(this, VideoPlayerActivity::class.java).apply {
                putExtra("videoUri", videoUri)
            }
            startActivity(intent)
        }

        // Ustaw widoczność TextView w zależności od stanu listy mediów
        if (videos.isEmpty()) emptyTextView.visibility = View.VISIBLE
        else emptyTextView.visibility = View.GONE
    }

    private fun configureRecyclerView() {
        val orientation = resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) 5 else 3
        albumContentRecyclerView.layoutManager = GridLayoutManager(this, spanCount)
    }

    private fun fetchImages(albumId: String?): List<Uri> {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)

        val selection = "${MediaStore.Images.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            if (albumId != null) selection else null,
            if (albumId != null) selectionArgs else null,
            MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(contentUri)
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