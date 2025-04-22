package com.study.playlistmaker.main.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.root_fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController

        binding.bottomNavigationView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.newPlaylistFragment || destination.id == R.id.playerFragment) {
                binding.bottomNavigationView.animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction {
                        binding.bottomNavigationView.isVisible = false
                        binding.bottomNavigationView.alpha = 1f
                    }
                    .start()
            } else if (!binding.bottomNavigationView.isVisible) {
                binding.bottomNavigationView.alpha = 0f
                binding.bottomNavigationView.isVisible = true
                binding.bottomNavigationView.animate()
                    .alpha(1f)
                    .setDuration(300)
                    .start()
            }
        }
    }
}
