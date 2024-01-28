package com.example.snapgallery.fragment

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.R
import com.example.snapgallery.adapter.VideoAdapter
import com.google.android.material.snackbar.Snackbar

class VideosFragment : Fragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                view?.let { loadVideos(it) }
            } else {
                view?.let {
                    Snackbar.make(it, "Brak przyznanych uprawnień do odczytu wideo", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_videos, container, false)

        val permission = Manifest.permission.READ_MEDIA_VIDEO
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            loadVideos(view)
        } else {
            permissionLauncher.launch(permission)
        }

        return view
    }

    private fun loadVideos(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.videosRecyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val videos = fetchVideos()
        val adapter = VideoAdapter(videos)
        recyclerView.adapter = adapter
    }

    private fun fetchVideos(): List<Uri> {
        val videoList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Video.Media._ID)

        val contentResolver = requireActivity().contentResolver
        contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
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
