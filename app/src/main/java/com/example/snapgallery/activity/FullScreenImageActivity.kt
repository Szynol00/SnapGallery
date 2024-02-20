package com.example.snapgallery.activity

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
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

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            // Obsługa kliknięcia przycisku wstecz, zazwyczaj zakończenie aktywności
            finish()
        }

        val shareButton = findViewById<ImageView>(R.id.share_button)
        shareButton.setOnClickListener {
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, ImageRepository.images[selectedImageIndex])
                type = "image/jpeg"
            }
            startActivity(Intent.createChooser(shareIntent, null))
        }

        val deleteButton = findViewById<ImageView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteCurrentImage()
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






