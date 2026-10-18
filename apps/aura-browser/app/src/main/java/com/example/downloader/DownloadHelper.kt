package com.example.downloader

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.webkit.URLUtil
import android.widget.Toast
import com.example.data.local.AppDatabase
import com.example.data.local.DownloadEntity
import com.example.data.model.ActiveDownload
import com.example.data.model.DownloadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

object DownloadHelper {

    /**
     * Resolves a clean filename from URL, Content-Disposition, and MimeType
     */
    fun guessFileName(url: String, contentDisposition: String?, mimeType: String?): String {
        var guessed = URLUtil.guessFileName(url, contentDisposition, mimeType)
        if (guessed.isNullOrBlank() || guessed == "downloadfile.bin") {
            val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType) ?: "mp4"
            guessed = "download_${System.currentTimeMillis()}.$extension"
        }
        return guessed
    }

    /**
     * Initiates download using Android System DownloadManager for background reliability
     */
    fun startSystemDownload(
        context: Context,
        url: String,
        userAgent: String?,
        contentDisposition: String?,
        mimeType: String?,
        scope: CoroutineScope,
        onDownloadStarted: (ActiveDownload) -> Unit
    ) {
        try {
            val fileName = guessFileName(url, contentDisposition, mimeType)
            val uri = Uri.parse(url)

            val request = DownloadManager.Request(uri).apply {
                setTitle(fileName)
                setDescription("Aura Browser Download")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
                setAllowedOverMetered(true)
                setAllowedOverRoaming(true)
                if (!userAgent.isNullOrBlank()) {
                    addRequestHeader("User-Agent", userAgent)
                }
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val downloadId = downloadManager.enqueue(request)

            val active = ActiveDownload(
                id = downloadId.toString(),
                fileName = fileName,
                url = url,
                mimeType = mimeType ?: "application/octet-stream",
                status = DownloadStatus.DOWNLOADING,
                filePath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/$fileName"
            )
            onDownloadStarted(active)

            // Save to room db
            scope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(context).downloadDao().insertDownload(
                    DownloadEntity(
                        fileName = fileName,
                        fileUrl = url,
                        filePath = active.filePath ?: "",
                        fileSize = 0L,
                        mimeType = mimeType ?: "unknown",
                        status = "COMPLETED"
                    )
                )
            }

            Toast.makeText(context, "Downloading: $fileName", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Download failed: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
