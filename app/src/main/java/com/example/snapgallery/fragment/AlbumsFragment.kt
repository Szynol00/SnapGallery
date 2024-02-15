package com.example.snapgallery.fragment

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.pm.PackageManager
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

        albumAdapter = AlbumAdapter(emptyList())
        recyclerView = view.findViewById(R.id.albumsRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 2)
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

        val projection = arrayOf(
            MediaStore.Images.Media.BUCKET_ID,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.Images.Media._ID
        )

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_ADDED} ASC"
        )

        cursor?.use {
            val bucketIdColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
            val bucketNameColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)

            while (it.moveToNext()) {
                val id = it.getString(bucketIdColumn)
                val name = it.getString(bucketNameColumn)
                val imageId = it.getLong(idColumn)
                val thumbnailUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, imageId)

                if (albumList.none { album -> album.id == id }) {
                    val imageCount = getMediaCount(contentResolver, id, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    val videoCount = getMediaCount(contentResolver, id, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                    val album = Album(id, name, imageCount, videoCount, thumbnailUri.toString())
                    albumList.add(album)
                }
            }
        }

        return albumList
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
