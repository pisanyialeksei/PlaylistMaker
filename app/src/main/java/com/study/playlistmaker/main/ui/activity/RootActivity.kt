package com.study.playlistmaker.main.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.ActivityRootBinding
import com.study.playlistmaker.search.ui.fragment.SearchFragment

class RootActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRootBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRootBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                this.add(R.id.rootFragmentContainerView, SearchFragment())
//                this.add(R.id.rootFragmentContainerView, LibraryFragment())
//                this.add(R.id.rootFragmentContainerView, SettingsFragment())
            }
        }
    }
}
