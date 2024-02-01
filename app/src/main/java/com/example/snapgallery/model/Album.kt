package com.example.snapgallery.model

data class Album(
    val id: String,
    val name: String,
    val photoCount: Int,
    val videoCount: Int,
    val thumbnailUri: String
)
