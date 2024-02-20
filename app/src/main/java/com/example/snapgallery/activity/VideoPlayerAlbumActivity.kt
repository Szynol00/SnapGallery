package com.example.snapgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.snapgallery.R

class VideoPlayerAlbumActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var albumVideosUris: ArrayList<Uri>
    private var currentVideoIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        // Ukryj przyciski przy starcie
        findViewById<ImageView>(R.id.prev_button).visibility = View.GONE
        findViewById<ImageView>(R.id.next_button).visibility = View.GONE


        playerView = findViewById(R.id.player_view)
        val prevButton = findViewById<ImageView>(R.id.prev_button)
        val nextButton = findViewById<ImageView>(R.id.next_button)

        albumVideosUris = intent.getParcelableArrayListExtra<Uri>("albumVideosUri") ?: arrayListOf()
        currentVideoIndex = intent.getIntExtra("selectedVideoIndex", 0)

        initializePlayer()

        prevButton.setOnClickListener {
            loadPreviousVideo()
        }

        nextButton.setOnClickListener {
            loadNextVideo()
        }

        // Ustawienie początkowej widoczności przycisków
        updateNavigationButtons()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().apply {
            playerView.player = this
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    if (playbackState == Player.STATE_ENDED) {
                        loadNextVideo()
                    }
                }
            })
        }
        playVideo(currentVideoIndex)
    }

    private fun loadNextVideo() {
        currentVideoIndex = (currentVideoIndex + 1) % albumVideosUris.size
        playVideo(currentVideoIndex)
        updateNavigationButtons()
    }

    private fun loadPreviousVideo() {
        currentVideoIndex = if (currentVideoIndex - 1 < 0) albumVideosUris.size - 1 else currentVideoIndex - 1
        playVideo(currentVideoIndex)
        updateNavigationButtons()
    }

    private fun playVideo(index: Int) {
        val mediaItem = MediaItem.fromUri(albumVideosUris[index])
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    private fun updateNavigationButtons() {
        // Logika aktualizacji widoczności przycisków, na przykład:
        findViewById<ImageView>(R.id.prev_button).visibility = if (albumVideosUris.size > 1) View.VISIBLE else View.INVISIBLE
        findViewById<ImageView>(R.id.next_button).visibility = if (albumVideosUris.size > 1) View.VISIBLE else View.INVISIBLE
    }


    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }

    private fun releasePlayer() {
        player?.release()
        player = null
    }



}
