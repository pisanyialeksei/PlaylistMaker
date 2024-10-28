package com.study.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.study.playlistmaker.data.ItunesSearchApiService
import com.study.playlistmaker.data.SearchResponse
import com.study.playlistmaker.track.Track
import com.study.playlistmaker.track.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var lastQuery: String

    private val itunesSearchApiService = Retrofit.Builder()
        .baseUrl("https://itunes.apple.com")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ItunesSearchApiService::class.java)

    private val trackList = mutableListOf<Track>()
    private val trackAdapter = TrackAdapter(trackList)

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyResultError: LinearLayout
    private lateinit var networkError: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        val clearButton = findViewById<ImageView>(R.id.search_edit_text_clear)
        clearButton.setOnClickListener {
            searchEditText.text.clear()
            val imm = searchEditText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearButton.isVisible = !s.isNullOrEmpty()
                searchText = s.toString()
                if (searchText.isEmpty()) {
                    val itemCount = trackList.size
                    trackList.clear()
                    trackAdapter.notifyItemRangeRemoved(0, itemCount)
                    recyclerView.isVisible = true
                    emptyResultError.isVisible = false
                    networkError.isVisible = false
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        recyclerView = findViewById(R.id.search_recycler_view)
        recyclerView.adapter = trackAdapter

        emptyResultError = findViewById(R.id.empty_search_result_error)
        networkError = findViewById(R.id.search_network_error)
        val networkErrorRefreshButton = findViewById<Button>(R.id.refresh_button)

        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE && searchText.isNotEmpty()) {
                performSearchRequest(searchText)
                lastQuery = searchText
            }
            false
        }
        networkErrorRefreshButton.setOnClickListener {
            performSearchRequest(lastQuery)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT_KEY, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT_KEY, "")
        val searchEditText = findViewById<EditText>(R.id.search_edit_text)
        searchEditText.setText(searchText)
    }

    private fun performSearchRequest(searchQuery: String) {
        itunesSearchApiService.search(searchQuery).enqueue(
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    if (response.code() == 200) {
                        trackList.clear()
                        val searchResults = response.body()?.results

                        if (!searchResults.isNullOrEmpty()) {
                            trackList.addAll(searchResults)
                            trackAdapter.notifyDataSetChanged()
                            recyclerView.isVisible = true
                            emptyResultError.isVisible = false
                            networkError.isVisible = false
                        }
                        if (trackList.isEmpty()) {
                            recyclerView.isVisible = false
                            emptyResultError.isVisible = true
                            networkError.isVisible = false
                        }
                    } else {
                        recyclerView.isVisible = false
                        emptyResultError.isVisible = false
                        networkError.isVisible = true
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    recyclerView.isVisible = false
                    emptyResultError.isVisible = false
                    networkError.isVisible = true
                }
            }
        )
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT_KEY"
    }
}
