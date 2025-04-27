package com.study.playlistmaker.search.data.impl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.study.playlistmaker.data.db.AppDatabase
import com.study.playlistmaker.search.data.dto.SearchRequest
import com.study.playlistmaker.search.data.dto.SearchResponse
import com.study.playlistmaker.search.data.network.NetworkClient
import com.study.playlistmaker.search.domain.SearchRepository
import com.study.playlistmaker.search.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent

class SearchRepositoryImpl(
    private val networkClient: NetworkClient,
    private val sharedPreferences: SharedPreferences,
    private val gson: Gson,
    private val appDatabase: AppDatabase,
) : SearchRepository, KoinComponent {

    private val historyKey = "SEARCH_HISTORY"

    override val currentHistory: MutableList<Track>
        get() = getHistory()

    override fun searchTracks(query: String): Flow<List<Track>?> = flow {
        val response = networkClient.doRequest(SearchRequest(query))
        if (response.resultCode == 200) {
            val favorites = appDatabase.favoritesDao().getFavoriteTrackIds()
            emit(
                (response as? SearchResponse)?.results?.map {
                    Track(
                        it.trackId,
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl
                    ).apply {
                        isFavorite = favorites.contains(it.trackId)
                    }
                } ?: emptyList()
            )
        } else {
            emit(null)
        }
    }

    override fun addTrackToHistory(track: Track) {
        val updatedHistory = currentHistory
        updatedHistory.removeAll { it.trackId == track.trackId }
        updatedHistory.add(0, track)
        if (updatedHistory.size > 10) {
            updatedHistory.removeAt(updatedHistory.size - 1)
        }
        val json = gson.toJson(updatedHistory)
        sharedPreferences.edit {
            putString(historyKey, json)
        }
    }

    override fun clearHistory() {
        sharedPreferences.edit {
            remove(historyKey)
        }
    }

    override suspend fun getFavoriteTrackIds(): List<Long> {
        return appDatabase.favoritesDao().getFavoriteTrackIds()
    }

    private fun getHistory(): MutableList<Track> {
        val json = sharedPreferences.getString(historyKey, null) ?: return mutableListOf()
        val type = object : TypeToken<MutableList<Track>>() {}.type
        return gson.fromJson(json, type)
    }
}
