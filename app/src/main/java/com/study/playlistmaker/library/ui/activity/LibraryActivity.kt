package com.study.playlistmaker.library.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.ActivityLibraryBinding
import com.study.playlistmaker.library.ui.adapter.LibraryViewPagerAdapter
import com.study.playlistmaker.sharing.data.StringProvider
import org.koin.android.ext.android.inject

class LibraryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    private val stringProvider: StringProvider by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.libraryViewPager.adapter = LibraryViewPagerAdapter(supportFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.libraryTabLayout, binding.libraryViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = stringProvider.getString(R.string.favorites)
                1 -> tab.text = stringProvider.getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroy() {
        super.onDestroy()
        tabMediator.detach()
    }
}
