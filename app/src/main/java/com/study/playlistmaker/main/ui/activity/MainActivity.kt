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
            val shouldHideBottomNavigation = destination.id in listOf(
                R.id.editPlaylistFragment,
                R.id.newPlaylistFragment,
                R.id.playerFragment,
                R.id.playlistFragment
            )

            when {
                shouldHideBottomNavigation && binding.bottomNavigationView.isVisible -> {
                    binding.bottomNavigationView.animate()
                        .alpha(0f)
                        .setDuration(300)
                        .withEndAction {
                            binding.bottomNavigationView.isVisible = false
                        }
                        .start()
                }
                !shouldHideBottomNavigation && !binding.bottomNavigationView.isVisible -> {
                    binding.bottomNavigationView.isVisible = true
                    binding.bottomNavigationView.animate()
                        .alpha(1f)
                        .setDuration(300)
                        .start()
                }
            }
        }
    }
}
