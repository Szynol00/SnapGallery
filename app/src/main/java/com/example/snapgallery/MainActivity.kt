package com.example.snapgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.example.snapgallery.databinding.ActivityMainBinding
import com.example.snapgallery.fragment.AlbumsFragment
import com.example.snapgallery.fragment.ImagesFragment
import com.example.snapgallery.fragment.MoviesFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        binding.bottomNavigationView.setOnItemSelectedListener {
            when(it.itemId) {
                R.id.images -> replaceFragment(ImagesFragment())
                R.id.movies -> replaceFragment(MoviesFragment())
                R.id.albums -> replaceFragment(AlbumsFragment())
                else -> {}
            }
            true
        }

        // Sprawdź czy savedInstanceState jest null, co oznacza że aplikacja została uruchomiona na nowo
        if (savedInstanceState == null) {
            // Wywołaj replaceFragment, aby wyświetlić Images Fragment jako startowy
            replaceFragment(ImagesFragment())
            // Ustaw wybrany element na BottomNavigationView
            binding.bottomNavigationView.selectedItemId = R.id.images
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.top_tool_bar, menu)
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, fragment)
        fragmentTransaction.commit()
    }
}
