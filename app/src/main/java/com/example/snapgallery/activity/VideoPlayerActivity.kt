package com.example.snapgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.snapgallery.R
import com.example.snapgallery.VideoRepository
import androidx.media3.ui.PlayerControlView


class VideoPlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)


        // Ukryj przyciski przy starcie
        findViewById<ImageView>(R.id.prev_button).visibility = View.GONE
        findViewById<ImageView>(R.id.next_button).visibility = View.GONE

        playerView = findViewById(R.id.player_view)

        val prevButton = findViewById<ImageView>(R.id.prev_button)
        val nextButton = findViewById<ImageView>(R.id.next_button)
        val backArrow = findViewById<ImageView>(R.id.backArrow)

        backArrow.setOnClickListener {
            finish()
        }
        prevButton.setOnClickListener {
            loadPreviousVideo()
        }

        nextButton.setOnClickListener {
            loadNextVideo()
        }

        initializePlayer()
    }


    @OptIn(UnstableApi::class) private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer

            // Dodaj tutaj Listener dla widoczności kontrolerów odtwarzacza
            playerView.setControllerVisibilityListener(PlayerControlView.VisibilityListener { visibility ->
                // Logika pokazywania/ukrywania przycisków
                val isVisible = visibility == View.VISIBLE
                findViewById<ImageView>(R.id.prev_button).visibility = if (isVisible) View.VISIBLE else View.GONE
                findViewById<ImageView>(R.id.next_button).visibility = if (isVisible) View.VISIBLE else View.GONE
            })


            exoPlayer.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(playbackState: Int) {
                    super.onPlaybackStateChanged(playbackState)
                    // Twoja logika związana ze zmianą stanu odtwarzania
                }

                override fun onPlayerError(error: PlaybackException) {
                    runOnUiThread {
                        Toast.makeText(this@VideoPlayerActivity, "Playback Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })

            // Ładowanie i odtwarzanie wideo
            val videoUri: Uri = intent.getParcelableExtra("videoUri") ?: run {
                finish()
                return
            }
            val mediaItem = MediaItem.fromUri(videoUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
        }

        val shareButton = findViewById<ImageView>(R.id.share_button)
        shareButton.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("videoUri")
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "video/mp4"
                if (videoUri != null) {
                    putExtra(Intent.EXTRA_STREAM, videoUri as Parcelable)
                }
            }
            if (shareIntent.resolveActivity(packageManager) != null) {
                startActivity(Intent.createChooser(shareIntent, null))
            } else {
                Toast.makeText(this@VideoPlayerActivity, "No app available to share video", Toast.LENGTH_SHORT).show()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        if (player == null) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        if (player == null) {
            initializePlayer()
        }
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
        player?.let {
            it.release()
            player = null
        }
    }

    private fun loadNextVideo() {
        // Zwiększ indeks odtwarzanego filmu i załaduj film
        // Zakładamy, że masz dostęp do listy filmów i aktualnego indeksu
        // Na przykład za pomocą VideoRepository.selectedVideoIndex
        VideoRepository.selectedVideoIndex = (VideoRepository.selectedVideoIndex + 1) % VideoRepository.videos.size
        val nextVideoUri = VideoRepository.videos[VideoRepository.selectedVideoIndex]
        playVideo(nextVideoUri)
    }

    private fun loadPreviousVideo() {
        // Zmniejsz indeks odtwarzanego filmu i załaduj film
        VideoRepository.selectedVideoIndex = if (VideoRepository.selectedVideoIndex - 1 < 0) VideoRepository.videos.size - 1 else VideoRepository.selectedVideoIndex - 1
        val prevVideoUri = VideoRepository.videos[VideoRepository.selectedVideoIndex]
        playVideo(prevVideoUri)
    }

    private fun playVideo(videoUri: Uri) {
        val mediaItem = MediaItem.fromUri(videoUri)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.play()
    }

    private fun updateNavigationButtonsVisibility(playbackState: Int) {
        // Sprawdzamy, czy odtwarzacz jest w stanie pauzy
        val isPlaying = playbackState == Player.STATE_READY && player?.playWhenReady == true

        // Pokazujemy lub ukrywamy przyciski na podstawie tego, czy odtwarzacz jest zatrzymany
        findViewById<ImageView>(R.id.prev_button).visibility = if (!isPlaying) View.VISIBLE else View.INVISIBLE
        findViewById<ImageView>(R.id.next_button).visibility = if (!isPlaying) View.VISIBLE else View.INVISIBLE
    }



}
