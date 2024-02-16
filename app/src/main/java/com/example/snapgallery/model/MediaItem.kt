package com.example.snapgallery.model

import android.net.Uri

data class MediaItem(
    val id: Long,
    val uri: Uri,
    val type: MediaType
)

enum class MediaType {
    IMAGE, VIDEO
}
