package com.example.snapgallery.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.example.snapgallery.activity.AlbumDetailsActivity
import com.example.snapgallery.adapter.AlbumAdapter
import com.example.snapgallery.model.Album
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AlbumsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var albumAdapter: AlbumAdapter

    private val permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
        if (isGranted) {
            loadAlbums()
        } else {
            Snackbar.make(requireView(), "Brak przyznanych uprawnień do odczytu albumów", Snackbar.LENGTH_LONG).show()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_albums, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchEditText: EditText = view.findViewById(R.id.searchEditText)

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                albumAdapter.filter(s.toString())
            }
        })

        albumAdapter = AlbumAdapter(emptyList(), object : AlbumAdapter.OnAlbumClickListener {
            override fun onAlbumClick(album: Album) {
                // Tutaj obsługuj kliknięcie, np. rozpoczynając nową aktywność z nazwą albumu
                val intent = Intent(context, AlbumDetailsActivity::class.java).apply {
                    putExtra("album_name", album.name)
                    putExtra("album_id", album.id) // Dodaj tę linię
                }

                startActivity(intent)
            }
        })

        recyclerView = view.findViewById(R.id.albumsRecyclerView)
        val orientation = resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            3 // Poziome położenie
        } else {
            2 // Pionowe położenie
        }
        recyclerView.layoutManager = GridLayoutManager(context, spanCount)
        recyclerView.adapter = albumAdapter

        checkPermissionAndLoadAlbums()
    }


    private fun checkPermissionAndLoadAlbums() {
        val permission = if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            Manifest.permission.READ_EXTERNAL_STORAGE
        } else {
            Manifest.permission.READ_MEDIA_IMAGES
        }

        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            loadAlbums()
        } else {
            permissionLauncher.launch(permission)
        }
    }

    private fun loadAlbums() {
        CoroutineScope(Dispatchers.IO).launch {
            val albums = fetchAlbums()
            withContext(Dispatchers.Main) {
                albumAdapter.updateData(albums)
            }
        }
    }

    private fun fetchAlbums(): List<Album> {
        val albumList = mutableListOf<Album>()
        val contentResolver = requireActivity().contentResolver

        // Zdefiniuj projekcję dla obrazów i wideo
        val imageProjection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID
        )

        val videoProjection = arrayOf(
            MediaStore.Video.Media.BUCKET_ID,
            MediaStore.Video.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Video.Media._ID
        )

        // Pobierz albumy z obrazami
        fetchMediaAlbums(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageProjection, albumList)

        // Pobierz albumy z wideo
        fetchMediaAlbums(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI, videoProjection, albumList)

        return albumList
    }

    private fun fetchMediaAlbums(contentResolver: ContentResolver, uri: Uri, projection: Array<String>, albumList: MutableList<Album>) {
        val cursor = contentResolver.query(uri, projection, null, null, "${MediaStore.Images.Media.DATE_ADDED} ASC")

        cursor?.use {
            val bucketIdColumn = it.getColumnIndexOrThrow(projection[0])
            val bucketNameColumn = it.getColumnIndexOrThrow(projection[1])
            val idColumn = it.getColumnIndexOrThrow(projection[2])

            while (it.moveToNext()) {
                val id = it.getString(bucketIdColumn)
                val name = it.getString(bucketNameColumn)
                val mediaId = it.getLong(idColumn)
                val thumbnailUri = ContentUris.withAppendedId(uri, mediaId)

                val existingAlbum = albumList.find { album -> album.id == id }
                if (existingAlbum == null) {
                    val imageCount = getMediaCount(contentResolver, id, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    val videoCount = getMediaCount(contentResolver, id, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    val album = Album(id, name, imageCount, videoCount, thumbnailUri.toString())
                    albumList.add(album)
                }
            }
        }
    }


    private fun getMediaCount(contentResolver: ContentResolver, albumId: String, uri: Uri): Int {
        val projection = arrayOf(MediaStore.MediaColumns._ID)
        val selection = "${MediaStore.MediaColumns.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(albumId)

        return contentResolver.query(
            uri,
            projection,
            selection,
            selectionArgs,
            null
        )?.use { cursor ->
            cursor.count
        } ?: 0
    }
}
