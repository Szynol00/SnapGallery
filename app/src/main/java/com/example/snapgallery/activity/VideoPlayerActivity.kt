package com.example.snapgallery.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AlertDialog
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
import java.io.File
import java.io.FileNotFoundException


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

        val deleteButton = findViewById<ImageView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteCurrentVideo()
        }

        val infoButton = findViewById<ImageView>(R.id.info_button)



        backArrow.setOnClickListener {
            finish()
        }
        prevButton.setOnClickListener {
            loadPreviousVideo()
        }

        nextButton.setOnClickListener {
            loadNextVideo()
        }

        infoButton.setOnClickListener {
            val videoUri: Uri? = intent.getParcelableExtra("videoUri")
            videoUri?.let { uri ->
                showVideoInfo(uri)
            }
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

        val deleteButton = findViewById<ImageView>(R.id.delete_button)
        deleteButton.setOnClickListener {
            deleteCurrentVideo()
        }

    }


    private fun deleteCurrentVideo() {
        val currentVideoUri = VideoRepository.getVideo(VideoRepository.selectedVideoIndex)

        currentVideoUri?.let { uri ->
            try {
                val rowsDeleted = contentResolver.delete(uri, null, null)
                if (rowsDeleted == 1) {
                    Toast.makeText(this, "Wideo zostało pomyślnie usunięte.", Toast.LENGTH_SHORT).show()

                    // Aktualizacja repozytorium po usunięciu wideo
                    VideoRepository.removeVideoAt(VideoRepository.selectedVideoIndex)

                    // Załaduj następne wideo lub zakończ, jeśli to było ostatnie wideo
                    if (VideoRepository.videos.isNotEmpty()) {
                        VideoRepository.selectedVideoIndex = (VideoRepository.selectedVideoIndex) % VideoRepository.videos.size
                        playVideo(VideoRepository.getVideo(VideoRepository.selectedVideoIndex)!!)
                    } else {
                        finish()
                    }
                } else {
                    Toast.makeText(this, "Nie udało się usunąć wideo.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: SecurityException) {
                Toast.makeText(this, "Brak uprawnień do usunięcia wideo.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Wystąpił problem podczas usuwania wideo.", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun showVideoInfo(videoUri: Uri) {
        val fileName = videoUri.lastPathSegment ?: "Niedostępne"

        try {
            contentResolver.openFileDescriptor(videoUri, "r")?.use { parcelFileDescriptor ->
                val fileSize = parcelFileDescriptor.statSize // Rozmiar pliku w bajtach
                val fileSizeInKB = fileSize / 1024
                val fileSizeInMB = fileSizeInKB / 1024

                // Przygotowanie i wyświetlenie informacji o wideo
                val fileInfo = StringBuilder().apply {
                    append("Nazwa pliku: $fileName\n")
                    append("Ścieżka/URI: $videoUri\n")
                    append("Rozmiar wideo: ${fileSizeInKB}KB (${fileSizeInMB}MB)")
                }.toString()

                // Użycie AlertDialog do wyświetlenia informacji
                AlertDialog.Builder(this).apply {
                    setTitle("Informacje o wideo")
                    setMessage(fileInfo)
                    setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                    show()
                }
            }
        } catch (e: FileNotFoundException) {
            Toast.makeText(this, "Plik wideo nie został znaleziony.", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Błąd podczas odczytywania informacji o wideo: ${e.message}", Toast.LENGTH_LONG).show()
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


