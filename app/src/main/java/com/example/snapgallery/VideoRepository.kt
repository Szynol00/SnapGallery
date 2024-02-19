package com.example.snapgallery

import android.net.Uri

object VideoRepository {
    var videos: List<Uri> = emptyList()
    var selectedVideoIndex: Int = -1 // Kotlin automatycznie generuje getter i setter dla tej zmiennej

    fun addVideo(uri: Uri) {
        videos = videos + uri
    }

    fun removeVideo(uri: Uri) {
        videos = videos.filter { it != uri }
    }

    fun getVideo(index: Int): Uri? = videos.getOrNull(index)
}

