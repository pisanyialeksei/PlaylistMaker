package com.study.playlistmaker.library.domain.storage

import android.net.Uri
import java.io.File

interface PlaylistStorageService {

    fun getCoverFile(playlistName: String): File
    fun saveImageToPrivateStorage(uri: Uri, outputFile: File)
    fun deleteCoverFile(path: String)
}
