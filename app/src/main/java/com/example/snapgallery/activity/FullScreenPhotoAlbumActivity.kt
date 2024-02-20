package com.example.snapgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.snapgallery.R
import com.example.snapgallery.adapter.FullScreenPhotoAlbumAdapter

class FullScreenPhotoAlbumActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var albumImagesUris: ArrayList<Uri>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full_screen_image)

        // Pobieranie listy Uri obrazów
        albumImagesUris = intent.extras?.getParcelableArrayList<Uri>("albumImagesUris") ?: arrayListOf()

        // Pobieranie indeksu wybranego obrazu
        val selectedImageIndex: Int = intent.getIntExtra("selectedImageIndex", 0)

        viewPager = findViewById(R.id.viewPager)
        viewPager.adapter = FullScreenPhotoAlbumAdapter(this, albumImagesUris)

        // Ustawienie ViewPager2 na wybranym obrazie
        viewPager.currentItem = selectedImageIndex

        val backArrow: ImageView = findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            finish()
        }

        val shareButton = findViewById<ImageView>(R.id.share_button)
        shareButton.setOnClickListener {
            val currentImageUri = albumImagesUris[viewPager.currentItem]
            val shareIntent: Intent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, currentImageUri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(shareIntent, resources.getText(R.string.share)))
        }
    }
}
