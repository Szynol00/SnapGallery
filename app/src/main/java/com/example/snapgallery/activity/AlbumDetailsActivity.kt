package com.example.snapgallery.activity

import android.content.ContentUris
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.example.snapgallery.adapter.MediaAdapter
import com.example.snapgallery.model.MediaItem
import com.example.snapgallery.model.MediaType

class AlbumDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_album_details)

        val albumNameTextView: TextView = findViewById(R.id.albumNameTextView)
        val albumContentRecyclerView: RecyclerView = findViewById(R.id.albumContentRecyclerView)

        // Pobierz album_id i album_name z intencji
        val albumId = intent.getStringExtra("album_id")
        val albumName = intent.getStringExtra("album_name")
        albumNameTextView.text = albumName ?: "Brak nazwy albumu"

        // Skonfiguruj RecyclerView z MediaAdapter
        albumContentRecyclerView.layoutManager = GridLayoutManager(this, 3)

        // Pobierz media dla danego albumu
        val mediaItems = fetchMedia(albumId) // Upewnij się, że ta metoda zwraca odpowiednie dane

        // Ustaw adapter z obsługą kliknięć na elementy mediów
        albumContentRecyclerView.adapter = MediaAdapter(mediaItems) { mediaItem ->
            // Tutaj zdefiniuj, co ma się dziać po kliknięciu na element
            // Na przykład możesz uruchomić aktywność z pełnym ekranem zdjęcia lub odtwarzanie filmu
        }
    }

    private fun fetchMedia(albumId: String?): List<MediaItem> {
        return fetchImages(albumId) + fetchVideos(albumId)
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

    private fun fetchVideos(albumId: String?): List<MediaItem> {
        val videoList = mutableListOf<MediaItem>()
        val projection = arrayOf(MediaStore.Video.Media._ID)

        val selection = "${MediaStore.Video.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        val cursor = contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            if (albumId != null) selection else null,
            if (albumId != null) selectionArgs else null,
            MediaStore.Video.Media.DATE_TAKEN + " DESC"
        )

        cursor?.use {
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            while (it.moveToNext()) {
                val id = it.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
                videoList.add(MediaItem(id, contentUri, MediaType.VIDEO))
            }
        }

        return videoList
    }


}

