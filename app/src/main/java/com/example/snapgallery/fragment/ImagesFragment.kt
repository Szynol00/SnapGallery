package com.example.snapgallery.fragment

import android.Manifest
import android.content.ContentUris
import android.content.Intent
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
import com.example.snapgallery.ImageRepository
import com.example.snapgallery.R
import com.example.snapgallery.adapter.ImageAdapter
import com.google.android.material.snackbar.Snackbar
import com.example.snapgallery.activity.FullScreenImageActivity


class ImagesFragment : Fragment() {


    // Inicjalizacja ActivityResultLauncher do obsługi wyniku żądania uprawnienia
    private lateinit var permissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Rejestrujemy ActivityResultLauncher
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Jeśli uprawnienie zostało przyznane, załaduj obrazy
                view?.let { loadImages(it) }
            } else {
                // Obsługa sytuacji, gdy uprawnienie nie jest przyznane
                view?.let {
                    Snackbar.make(it, "Brak przyznanych uprawnień do odczytu zdjęć", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_images, container, false)

        // Użyj nowego uprawnienia do odczytu obrazów
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED) {
            loadImages(view)
        } else {
            // Jeśli nie, to żądaj uprawnienia
            permissionLauncher.launch(permission)
        }

        return view
    }

    // Metoda do ładowania obrazów do RecyclerView
    private fun loadImages(view: View) {
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val images = fetchImages()

        // Zapisywanie obrazów do repozytorium
        ImageRepository.images = images

        // Przekazanie funkcji obsługującej kliknięcie na obraz
        val adapter = ImageAdapter(images) { uri ->
            val selectedImageIndex = images.indexOf(uri)
            val intent = Intent(context, FullScreenImageActivity::class.java).apply {
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
