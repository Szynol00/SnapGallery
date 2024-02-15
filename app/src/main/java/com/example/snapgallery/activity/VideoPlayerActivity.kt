package com.example.snapgallery.activity

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.ui.PlayerView
import com.example.snapgallery.R

class VideoPlayerActivity : AppCompatActivity() {
    private lateinit var player: ExoPlayer
    private lateinit var playerView: PlayerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)

        playerView = findViewById(R.id.player_view)
        initializePlayer()
    }

    private fun initializePlayer() {
        player = ExoPlayer.Builder(this).build()
        playerView.player = player

        // Add the listener to the player
        player.addListener(object : Player.Listener {
            override fun onPlayerError(error: PlaybackException) {
                // Handle error. For example, show a message to the user.
                runOnUiThread {
                    Toast.makeText(this@VideoPlayerActivity, "Playback Error: ${error.message}", Toast.LENGTH_LONG).show()
                }
            }
        })

        val videoUri: Uri = intent.getParcelableExtra("videoUri") ?: run {
            // Log error or show user feedback
            Toast.makeText(this, "Video URI is missing", Toast.LENGTH_SHORT).show()
            finish() // Close the activity as there's no video to play
            return
        }
        val mediaItem = MediaItem.fromUri(videoUri)
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }



    @OptIn(UnstableApi::class) override fun onStart() {
        super.onStart()
        if (Util.SDK_INT > 23) {
            initializePlayer()
            playerView.onResume() // If you're using PlayerView's features that require onResume
        }
    }

    @OptIn(UnstableApi::class) override fun onResume() {
        super.onResume()
        if (Util.SDK_INT <= 23 || player == null) {
            initializePlayer()
            playerView.onResume() // If you're using PlayerView's features that require onResume
        }
    }

    @OptIn(UnstableApi::class) override fun onPause() {
        super.onPause()
        if (Util.SDK_INT <= 23) {
            playerView.onPause() // If you're using PlayerView's features that require onPause
            releasePlayer()
        }
    }

    @OptIn(UnstableApi::class) override fun onStop() {
        super.onStop()
        if (Util.SDK_INT > 23) {
            playerView.onPause() // If you're using PlayerView's features that require onPause
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player.release()
        // Null out the player instance if you're re-initializing it in initializePlayer
    }

}
