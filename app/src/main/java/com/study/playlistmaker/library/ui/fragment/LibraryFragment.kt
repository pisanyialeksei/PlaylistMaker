package com.study.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.study.playlistmaker.R
import com.study.playlistmaker.databinding.FragmentLibraryBinding
import com.study.playlistmaker.library.ui.adapter.LibraryViewPagerAdapter
import com.study.playlistmaker.sharing.data.StringProvider
import org.koin.android.ext.android.inject

class LibraryFragment : Fragment() {

    private lateinit var binding: FragmentLibraryBinding
    private lateinit var tabMediator: TabLayoutMediator

    private val stringProvider: StringProvider by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.libraryViewPager.adapter = LibraryViewPagerAdapter(childFragmentManager, lifecycle)

        tabMediator = TabLayoutMediator(binding.libraryTabLayout, binding.libraryViewPager) { tab, position ->
            when (position) {
                0 -> tab.text = stringProvider.getString(R.string.favorites)
                1 -> tab.text = stringProvider.getString(R.string.playlists)
            }
        }
        tabMediator.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        tabMediator.detach()
    }

    companion object {

        fun newInstance() = LibraryFragment()
    }
}
