package com.example.snapgallery

import android.net.Uri

object VideoRepository {
    var videos: MutableList<Uri> = mutableListOf()
    var selectedVideoIndex: Int = -1

    fun addVideo(uri: Uri) {
        videos.add(uri)
    }

    fun removeVideoAt(index: Int) {
        if (videos.size > index) {
            videos.removeAt(index)
        }
    }

    fun getVideo(index: Int): Uri? = videos.getOrNull(index)
}

