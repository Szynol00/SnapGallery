package com.example.snapgallery.activity

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.example.snapgallery.ImageRepository
import com.example.snapgallery.R
import com.example.snapgallery.adapter.FullScreenImageAdapter

class FullScreenImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        val selectedImageIndex = intent.getIntExtra("selectedImageIndex", 0)

        // Używanie obrazów z repozytorium
        val adapter = FullScreenImageAdapter(this, ImageRepository.images)

        val viewPager: ViewPager2 = findViewById(R.id.viewPager)
        viewPager.adapter = adapter
        viewPager.setCurrentItem(selectedImageIndex, false)

        val backArrow = findViewById<ImageView>(R.id.backArrow)
        backArrow.setOnClickListener {
            // Obsługa kliknięcia przycisku wstecz, zazwyczaj zakończenie aktywności
            finish()
        }

    }
}





