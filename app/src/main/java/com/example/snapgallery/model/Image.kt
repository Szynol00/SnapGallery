package com.example.snapgallery.model

import android.net.Uri

data class Image(
    val uri: Uri,
    val title: String,
    val dateTaken: Long    // Data wykonania zdjęcia (w milisekundach)
)
