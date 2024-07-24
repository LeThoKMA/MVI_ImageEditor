package com.example.mviimageeditor.download

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.mviimageeditor.utils.FILE_TITLE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.UUID

class DownloadServiceImpl(private val context: Context) : DownloadService {
    override suspend fun downloadImage(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(FILE_TITLE)
        val fileName = "image/${UUID.randomUUID()}.jpg"
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
        val downloadManager =
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)
    }

    @SuppressLint("NewApi")
    override suspend fun saveImage(bitmap: Bitmap) {
        val fileName = "image/${UUID.randomUUID()}.png"
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // For Android versions lower than Q, handle the file saving differently
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "$fileName.png")
            Uri.fromFile(file)
        }
        if (uri != null) {
            try {
                resolver.openOutputStream(uri).use { output ->
                    if (output != null) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
                    }
                }
                Log.d("SaveFile", "File saved successfully: $uri")
            } catch (e: IOException) {
                Log.e("SaveFile", "Error saving file", e)
            }
        } else {
            Log.e("SaveFile", "Failed to create URI")
        }
    }

    private fun saveFileUsingMediaStore(context: Context, url: String, fileName: String) {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }
        val resolver = context.contentResolver
        val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
        } else {
            // For Android versions lower than Q, handle the file saving differently
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val file = File(downloadsDir, "$fileName.png")
            Uri.fromFile(file)
        }
        if (uri != null) {
            try {
                URL(url).openStream().use { input ->
                    resolver.openOutputStream(uri).use { output ->
                        input.copyTo(output!!, DEFAULT_BUFFER_SIZE)
                    }
                }
                Log.d("SaveFile", "File saved successfully: $uri")
            } catch (e: IOException) {
                Log.e("SaveFile", "Error saving file", e)
            }
        } else {
            Log.e("SaveFile", "Failed to create URI")
        }
    }


}
