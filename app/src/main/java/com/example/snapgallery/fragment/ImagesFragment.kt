package com.example.snapgallery.fragment

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.snapgallery.ImageRepository
import com.example.snapgallery.R
import com.example.snapgallery.adapter.ImageAdapter
import com.google.android.material.snackbar.Snackbar
import com.example.snapgallery.activity.FullScreenImageActivity

class ImagesFragment : Fragment() {

    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                view?.let { loadImages(it) }
            } else {
                view?.let {
                    Snackbar.make(it, "Brak przyznanych uprawnień do odczytu zdjęć", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_images, container, false)
        checkAndRequestPermissions(view)
        return view
    }

    private fun checkAndRequestPermissions(view: View) {
        val permissionsNeeded = mutableListOf<String>()

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // Dla Android 11 i nowszych, uprawnienia są zarządzane przez nowe API
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        }

        if (permissionsNeeded.isNotEmpty()) {
            permissionLauncher.launch(permissionsNeeded.first())
        } else {
            loadImages(view)
        }
    }

    // Metoda do ładowania obrazów do RecyclerView
    private fun loadImages(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val orientation = resources.configuration.orientation
        val spanCount = if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            5 // Poziome położenie
        } else {
            3 // Pionowe położenie
        }

        recyclerView.layoutManager = GridLayoutManager(context, spanCount)

        val images = fetchImages()

        // Zapisywanie obrazów do repozytorium
        ImageRepository.images = images

        // Przekazanie funkcji obsługującej kliknięcie na obraz
        val adapter = ImageAdapter(images) { uris, uri ->
            val selectedImageIndex = uris.indexOf(uri)
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
                putParcelableArrayListExtra("albumImagesUris", uris)
                putExtra("selectedImageIndex", selectedImageIndex) // Przekazanie indeksu wybranego obrazu
            }
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }

    // Metoda do pobierania obrazów z urządzenia
    private fun fetchImages(): List<Uri> {
        val imageList = mutableListOf<Uri>()
        val projection = arrayOf(MediaStore.Images.Media._ID)

        // Używamy contentResolver z kontekstu aktywności
        val contentResolver = requireActivity().contentResolver

        // Zapytanie do MediaStore o obrazy
        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageList.add(contentUri)
            }
        }

        return imageList
    }
}
