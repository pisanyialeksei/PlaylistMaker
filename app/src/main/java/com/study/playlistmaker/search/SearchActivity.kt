package com.study.playlistmaker.search

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
import com.study.playlistmaker.R
import com.study.playlistmaker.SHARED_PREFERENCES
import com.study.playlistmaker.search.data.RetrofitClient
import com.study.playlistmaker.search.data.SearchResponse
import com.study.playlistmaker.track.Track
import com.study.playlistmaker.track.TrackAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {

    private var searchText: String = ""
    private lateinit var lastQuery: String

    private val searchList = mutableListOf<Track>()
    private val searchAdapter = TrackAdapter(searchList)
    private lateinit var historyAdapter: TrackAdapter
    private lateinit var searchHistoryManager: SearchHistoryManager

    private lateinit var searchRecyclerView: RecyclerView
    private lateinit var emptyResultError: LinearLayout
    private lateinit var networkError: LinearLayout
    private lateinit var historyView: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE)
        searchHistoryManager = SearchHistoryManager(sharedPreferences)
        searchAdapter.setSearchHistoryManager(searchHistoryManager)
        historyAdapter = TrackAdapter(searchHistoryManager.currentHistory)
        historyAdapter.setSearchHistoryManager(searchHistoryManager)

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
                    val itemCount = searchList.size
                    searchList.clear()
                    searchAdapter.notifyItemRangeRemoved(0, itemCount)
                    emptyResultError.isVisible = false
                    networkError.isVisible = false
                    if (searchEditText.hasFocus() && searchHistoryManager.currentHistory.isNotEmpty()) {
                        historyAdapter.updateData(searchHistoryManager.currentHistory)
                        searchRecyclerView.isVisible = false
                        historyView.isVisible = true
                    } else {
                        searchRecyclerView.isVisible = true
                    }
                }

            }

            override fun afterTextChanged(s: Editable?) {}
        }

        searchRecyclerView = findViewById(R.id.search_recycler_view)
        searchRecyclerView.adapter = searchAdapter

        historyView = findViewById(R.id.search_history)
        historyRecyclerView = findViewById(R.id.search_history_recycler_view)
        historyRecyclerView.adapter = historyAdapter
        val clearHistoryButton = findViewById<Button>(R.id.clear_history_button)
        clearHistoryButton.setOnClickListener {
            searchHistoryManager.clearHistory()
            historyAdapter.notifyItemRangeRemoved(0, historyAdapter.itemCount)
            historyView.isVisible = false
        }

        emptyResultError = findViewById(R.id.empty_search_result_error)
        networkError = findViewById(R.id.search_network_error)
        val networkErrorRefreshButton = findViewById<Button>(R.id.refresh_button)

        searchEditText.addTextChangedListener(textWatcher)
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && searchText.isEmpty() && searchHistoryManager.currentHistory.isNotEmpty()) {
                historyAdapter.notifyItemRangeChanged(0, 10)
                searchRecyclerView.isVisible = false
                historyView.isVisible = true
                emptyResultError.isVisible = false
                networkError.isVisible = false
            } else {
                searchRecyclerView.isVisible = true
                historyView.isVisible = false
                emptyResultError.isVisible = false
                networkError.isVisible = false
            }
        }
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
        RetrofitClient.itunesSearchApiService.search(searchQuery).enqueue(
            object : Callback<SearchResponse> {
                override fun onResponse(
                    call: Call<SearchResponse>,
                    response: Response<SearchResponse>
                ) {
                    if (response.code() == 200) {
                        searchList.clear()
                        val searchResults = response.body()?.results

                        if (!searchResults.isNullOrEmpty()) {
                            searchList.addAll(searchResults)
                            searchAdapter.notifyDataSetChanged()
                            searchRecyclerView.isVisible = true
                            historyView.isVisible = false
                            emptyResultError.isVisible = false
                            networkError.isVisible = false
                        }
                        if (searchList.isEmpty()) {
                            searchRecyclerView.isVisible = false
                            historyView.isVisible = false
                            emptyResultError.isVisible = true
                            networkError.isVisible = false
                        }
                    } else {
                        searchRecyclerView.isVisible = false
                        historyView.isVisible = false
                        emptyResultError.isVisible = false
                        networkError.isVisible = true
                    }
                }

                override fun onFailure(call: Call<SearchResponse>, t: Throwable) {
                    searchRecyclerView.isVisible = false
                    historyView.isVisible = false
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
