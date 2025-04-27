package com.study.playlistmaker.library.data.impl

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import com.study.playlistmaker.library.domain.storage.PlaylistStorageService
import java.io.File
import java.io.FileOutputStream

class PlaylistStorageServiceImpl(private val context: Context) : PlaylistStorageService {

    override fun getCoverFile(playlistName: String): File {
        val fileName = "playlist_${playlistName.replace(" ", "_").lowercase()}.jpg"
        val directoryPath = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlists")

        if (!directoryPath.exists()) {
            directoryPath.mkdirs()
        }

        return File(directoryPath, fileName)
    }

    override fun saveImageToPrivateStorage(uri: Uri, outputFile: File) {
        val inputStream = context.contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(outputFile)
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

    override fun deleteCoverFile(path: String) {
        val file = File(path)
        if (file.exists()) {
            file.delete()
        }
    }
}
