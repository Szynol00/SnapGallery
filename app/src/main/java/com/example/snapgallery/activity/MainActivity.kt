package com.example.snapgallery.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.snapgallery.R
import com.example.snapgallery.databinding.ActivityMainBinding
import com.example.snapgallery.fragment.AlbumsFragment
import com.example.snapgallery.fragment.ImagesFragment
import com.example.snapgallery.fragment.VideosFragment


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

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
            when(it.itemId) {
                R.id.images -> replaceFragment(ImagesFragment())
                R.id.movies -> replaceFragment(VideosFragment())
                R.id.albums -> replaceFragment(AlbumsFragment())
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
