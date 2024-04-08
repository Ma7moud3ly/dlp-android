package dlp.android.ma7moud3ly

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class DownloadManager private constructor(
    private val downloadsPath: String,
    private val python: Python
) {
    private val cacheFile = File(File(downloadsPath).parentFile, "media_info.json")

    private var downloaderScript = python.getModule("downloader")
    val errorFlow = MutableSharedFlow<String>(replay = 0)
    val downloadCompleteFlow = MutableSharedFlow<Unit>(1)
    val downloadProgressFlow = MutableSharedFlow<DownloadProgress?>(1)

    companion object {
        private const val TAG = "DownloadManager"
        lateinit var instance: DownloadManager
            private set

        fun init(context: Context, downloadsPath: String) {
            if (!Python.isStarted()) Python.start(AndroidPlatform(context))
            instance = DownloadManager(
                python = Python.getInstance(),
                downloadsPath = downloadsPath
            )
        }
    }

    suspend fun terminateExecution() {
        withContext(Dispatchers.IO) {
            try {
                downloaderScript.callAttr("stopDownload")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun downloadProgress(downloaded: Int, total: Int, percent: Float) {
        val progress = DownloadProgress(
            downloaded = downloaded.toMega,
            size = total.toMega,
            percent = percent
        )
        downloadProgressFlow.tryEmit(progress)
    }

    private fun downloadComplete() {
        downloadCompleteFlow.tryEmit(Unit)
        Log.i(TAG, "downloadComplete")
    }

    suspend fun download(url: String, formatId: String) {
        withContext(Dispatchers.IO) {
            try {
                downloaderScript["download_progress"] = ::downloadProgress.asPy
                downloaderScript["download_complete"] = ::downloadComplete.asPy
                downloaderScript.callAttr("startDownload", url, formatId, downloadsPath)
                downloadProgressFlow.emit(null)
            } catch (e: Exception) {
                e.printStackTrace()
                errorFlow.emit(e.message.orEmpty())
            }
        }
    }

    suspend fun getMediaInfo(url: String): MediaInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            val json = downloaderScript.callAttr("getInfo", url).toString()
            val mediaInfo = Gson().fromJson(json, MediaInfo::class.java)
            saveMediaInfo(json)
            mediaInfo
        } catch (e: Exception) {
            e.printStackTrace()
            errorFlow.emit(e.message.orEmpty())
            null
        }
    }

    private fun saveMediaInfo(mediaInfo: String) {
        try {
            val outputStream = FileOutputStream(cacheFile)
            outputStream.write(mediaInfo.toByteArray())
            outputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "File write failed: $e")
            e.printStackTrace()
        }
    }

    fun restoreMediaInfo(): MediaInfo? {
        if (cacheFile.exists().not()) return null
        return try {
            val inputStream = cacheFile.inputStream()
            val json = inputStream.bufferedReader().use { it.readText() }
            val mediaInfo = Gson().fromJson(json, MediaInfo::class.java)
            mediaInfo
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun clearMediaInfo() {
        if (cacheFile.exists()) cacheFile.delete()
    }


}

private val Any.asPy get() = PyObject.fromJava(this)
