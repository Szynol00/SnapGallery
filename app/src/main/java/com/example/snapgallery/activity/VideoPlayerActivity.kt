package com.example.snapgallery.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.snapgallery.R

class VideoPlayerActivity : AppCompatActivity() {
    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        playerView = findViewById(R.id.player_view)
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer
            exoPlayer.addListener(object : Player.Listener { // Zmieniono na Player.Listener
                override fun onPlaybackStateChanged(playbackState: Int) {
                    // Opcjonalnie, możesz tu obsłużyć zmiany stanu odtwarzania
                }

                override fun onPlayerError(error: PlaybackException) {
                    runOnUiThread {
                        Toast.makeText(this@VideoPlayerActivity, "Playback Error: ${error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            })
            val videoUri: Uri = intent.getParcelableExtra("videoUri") ?: run {
                Toast.makeText(this, "Video URI is missing", Toast.LENGTH_SHORT).show()
                finish()
                return
            }
            val mediaItem = MediaItem.fromUri(videoUri)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.play()
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
}
