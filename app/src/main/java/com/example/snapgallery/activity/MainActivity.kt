package com.example.snapgallery.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.snapgallery.R
import com.example.snapgallery.databinding.ActivityMainBinding
import com.example.snapgallery.fragment.AlbumsFragment
import com.example.snapgallery.fragment.ImagesFragment
import com.example.snapgallery.fragment.VideosFragment
import java.lang.String
import java.util.Locale
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var currentTab = "images"
    private var countClicks = 0
    private var isCounterRunning = false
    private val timer = object : CountDownTimer(500, 100) {
        override fun onTick(millisUntilFinished: Long) {}
        override fun onFinish() {
            isCounterRunning = false
            countClicks = 0
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicjalizacja widoku i bindowanie za pomocą View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ustawienie paska narzędziowego
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Ustawienie listenera dla BottomNavigationView
        binding.bottomNavigationView.setOnItemSelectedListener {
            // W zależności od wybranego elementu, wyświetl odpowiedni fragment
            when (it.itemId) {
                R.id.images -> {
                    if (currentTab == "images") countClicks()
                    else {
                        currentTab = "images"
                        replaceFragment(ImagesFragment())
                    }
                }

                R.id.movies -> {
                    if (currentTab == "videos") countClicks()
                    else {
                        currentTab = "videos"
                        replaceFragment(VideosFragment())
                    }
                }

                R.id.albums -> {
                    if (currentTab == "albums") countClicks()
                    else {
                        currentTab = "albums"
                        replaceFragment(AlbumsFragment())
                    }
                }

                else -> {}
            }
            true
        }

        // Sprawdzenie, czy istnieje zapisany stan instancji (np. po zmianie konfiguracji)
        if (savedInstanceState == null) {
            // Domyślnie wyświetla ImagesFragment
            replaceFragment(ImagesFragment())
            // Ustawienie domyślnego wybranego elementu w BottomNavigationView
            binding.bottomNavigationView.selectedItemId = R.id.images
        }
    }

    private fun countClicks() {
        timer.cancel()
        isCounterRunning = true
        timer.start()
        countClicks++

        if (countClicks >= 5) {
            // Vibrate for 500 milliseconds
            val v = getSystemService(VIBRATOR_SERVICE) as Vibrator

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(
                    VibrationEffect.createOneShot(
                        500,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )

                val intent = Intent(this, HiddenMultimedia::class.java)
                this.startActivity(intent)
            } else v.vibrate(500)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Tworzenie menu z pliku XML
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_tool_bar, menu)

        //  store the menu to var when creating options menu
        menu?.findItem(R.id.open_camera_button)?.setOnMenuItemClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            this.startActivity(intent)

            true
        }

        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        // Zarządzanie transakcjami fragmentów
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        // Zastąpienie fragmentu w kontenerze
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        // Potwierdzenie transakcji
        fragmentTransaction.commit()
    }
}
