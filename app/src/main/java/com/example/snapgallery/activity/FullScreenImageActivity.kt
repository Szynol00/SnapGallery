package com.example.snapgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.snapgallery.ImageRepository
import com.example.snapgallery.R
import com.example.snapgallery.adapter.FullScreenImageAdapter

class FullScreenImageActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val selectedImageIndex = intent.getIntExtra("selectedImageIndex", 0)

        // Używanie obrazów z repozytorium
        val adapter = FullScreenImageAdapter(this, ImageRepository.images)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(selectedImageIndex, false)

        setupBackButton()
        setupShareButton(selectedImageIndex)
        setupDeleteButton()
        setupInfoButton(selectedImageIndex) // Dodaj tę metodę do obsługi kliknięcia w ikonę informacji
    }

    private fun setupBackButton() {
        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }
    }

    private fun setupShareButton(selectedImageIndex: Int) {
        val shareButton = findViewById<ImageView>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, ImageRepository.images[selectedImageIndex])
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }
    }

    private fun setupDeleteButton() {
        val deleteButton = findViewById<ImageView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteCurrentImage()
        }
    }

    private fun setupInfoButton(selectedImageIndex: Int) {
        val infoButton = findViewById<ImageView>(R.id.info_button) // Upewnij się, że masz odpowiedni ID w swoim XML
        infoButton.setOnClickListener {
            // Wywołanie zmodyfikowanej funkcji, która pokazuje zarówno nazwę, jak i rozmiar zdjęcia
            showImageInfo(selectedImageIndex)
        }
    }

    private fun showImageInfo(index: Int) {
        val imageUri = ImageRepository.images[index]
        val imageName = imageUri.lastPathSegment ?: "Informacja niedostępna"

        try {
            contentResolver.openInputStream(imageUri)?.use { inputStream ->
                // Odczyt rozmiaru pliku
                val fileSize = inputStream.available() // Rozmiar w bajtach
                val fileSizeInKB = fileSize / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                // Przygotowanie i wyświetlenie informacji
                val fileInfo = StringBuilder().apply {
                    append("Nazwa pliku: $imageName\n")
                    append("URI: $imageUri\n") // Dodanie URI do informacji
                    append("Rozmiar: ${fileSizeInKB}KB (${fileSizeInMB}MB)")
                }.toString()

                // Użycie AlertDialog do wyświetlenia informacji
                AlertDialog.Builder(this).apply {
                    title = "Informacje o obrazie"
                    setMessage(fileInfo)
                    setPositiveButton("OK") { dialog, which -> dialog.dismiss() }
                    show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Błąd przy odczycie rozmiaru pliku: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }



    private fun deleteCurrentImage() {
        val currentPosition = viewPager.currentItem
        val imageUri = ImageRepository.images[currentPosition]

        // Usunięcie zdjęcia za pomocą ContentResolver
        try {
            val rowsDeleted = contentResolver.delete(imageUri, null, null)
            if (rowsDeleted == 1) {
                // Zdjęcie zostało pomyślnie usunięte, wyświetl komunikat
                Toast.makeText(this, "Zdjęcie zostało pomyślnie usunięte.", Toast.LENGTH_SHORT).show()

                // Kontynuacja aktualizacji UI
                val updatedList = ImageRepository.images.toMutableList().apply {
                    removeAt(currentPosition)
                }
                ImageRepository.images = updatedList

                viewPager.adapter = FullScreenImageAdapter(this, ImageRepository.images)
                viewPager.adapter?.notifyDataSetChanged()

                if (ImageRepository.images.isEmpty()) {
                    finish()
                    return
                }

                val newPosition = if (currentPosition >= ImageRepository.images.size) {
                    ImageRepository.images.size - 1
                } else {
                    currentPosition
                }

                viewPager.currentItem = newPosition
            } else {
                // Nie udało się usunąć zdjęcia
                Toast.makeText(this, "Nie udało się usunąć zdjęcia.", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            // Brak uprawnień lub inne problemy z bezpieczeństwem
            Toast.makeText(this, "Brak uprawnień do usunięcia zdjęcia.", Toast.LENGTH_SHORT).show()
        }
    }
}






