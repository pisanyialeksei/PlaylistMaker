package com.study.playlistmaker

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val searchButton = findViewById<Button>(R.id.search_button)
        val libraryButton = findViewById<Button>(R.id.library_button)
        val settingsButton = findViewById<Button>(R.id.settings_button)

        val searchButtonClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(view: View?) {
                Toast.makeText(this@MainActivity, "Search button clicked", Toast.LENGTH_SHORT).show()
            }
        }
        searchButton.setOnClickListener(searchButtonClickListener)
        libraryButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Library button clicked", Toast.LENGTH_SHORT).show()
        }
        settingsButton.setOnClickListener {
            Toast.makeText(this@MainActivity, "Settings button clicked", Toast.LENGTH_SHORT).show()
        }
    }
}
