package dlp.android.ma7moud3ly.managers

import android.content.Context
import android.util.Log
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import dlp.android.ma7moud3ly.data.DownloadProgress
import dlp.android.ma7moud3ly.data.MediaInfo
import dlp.android.ma7moud3ly.data.toMega
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

/**
 * Manages downloading and retrieving media information using (yt-dlp) video downloader, and
 * a python script (downloader.py) hosted by (chaquopy) library.
 *
 * @property downloadsPath The path where downloaded media files are stored.
 * @property python The Python interpreter instance.
 */
class DownloadManager private constructor(
    val downloadsPath: String,
    private val python: Python
) {
    // Create an object of (downloader.py) script that locates at app\src\main\python
    // to access python function that uses yt-dlp python library.
    private var downloaderScript = python.getModule("downloader")

    // This flow emits error messages encountered during the downloading process.
    val errorFlow = MutableSharedFlow<String>(replay = 0)

    //  This flow emits the completion status of downloads.
    val downloadCompleteFlow = MutableSharedFlow<Boolean?>(1)

    // This flow emits progress updates during the downloading process.
    val downloadProgressFlow = MutableSharedFlow<DownloadProgress?>(1)

    companion object {
        private const val TAG = "DownloadManager"

        lateinit var instance: DownloadManager
            private set

        /**
         * Initializes a static instance of DownloadManager.
         * @param context The application context.
         * @param downloadsPath The path where downloaded media files are stored.
         */
        fun init(context: Context, downloadsPath: String) {
            // Starts Python interpreter if not already started
            if (!Python.isStarted()) Python.start(AndroidPlatform(context))
            instance = DownloadManager(
                python = Python.getInstance(),
                downloadsPath = downloadsPath
            )
        }
    }

    /**
     * Python callbacks
     */

    // Handles download progress
    // This method overrides (download_progress) in (downloader.py)
    private fun downloadProgress(downloaded: Int, total: Int, percent: Float) {
        val progress = DownloadProgress(
            downloaded = downloaded.toMega,
            size = total.toMega,
            percent = percent
        )
        downloadProgressFlow.tryEmit(progress)
    }

    // Handles download completion
    // This method overrides (download_complete) in (downloader.py)
    private fun downloadComplete() {
        downloadCompleteFlow.tryEmit(true)
        Log.i(TAG, "downloadComplete")
    }

    // Handles error messages
    // This method overrides (download_error) in (downloader.py)
    private fun downloadError(msg: String) {
        errorFlow.tryEmit(msg)
        Log.i(TAG, "downloadError")
    }

    /**
     * Public functions for media downloader
     */

    /**
     * Terminates the ongoing download execution.
     */
    suspend fun terminateExecution() {
        withContext(Dispatchers.IO) {
            try {
                downloaderScript.callAttr("stopDownload")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Initiates downloading of a media file.
     * @param url The URL of the media file to download.
     * @param title The title of the media file.
     * @param formatId The format ID of the media file.
     */
    suspend fun download(url: String, title: String, formatId: String) {
        withContext(Dispatchers.IO) {
            try {
                downloaderScript["download_progress"] = ::downloadProgress.asPy
                downloaderScript["download_complete"] = ::downloadComplete.asPy
                downloaderScript["download_error"] = ::downloadError.asPy
                downloaderScript.callAttr(
                    "startDownload",
                    url,
                    formatId,
                    downloadsPath,
                    title
                )
                downloadProgressFlow.emit(null)
            } catch (e: Exception) {
                e.printStackTrace()
                errorFlow.emit(e.message.orEmpty())
            }
        }
    }

    /**
     * Retrieves media information for a given URL.
     * @param url The URL of the media file.
     * @return The MediaInfo object containing media information, or null if an error occurs.
     */
    suspend fun getMediaInfo(url: String): MediaInfo? = withContext(Dispatchers.IO) {
        return@withContext try {
            val json = downloaderScript.callAttr("getInfo", url).toString()
            val mediaInfo = Gson().fromJson(json, MediaInfo::class.java)
            mediaInfo
        } catch (e: Exception) {
            e.printStackTrace()
            errorFlow.emit(e.message.orEmpty())
            null
        }
    }

    /**
     * Retrieves the version of the yt-dlp python package.
     * @return yt-dlp version.
     */
    private var version = ""
    fun dlpVersion(): String {
        if (version.isEmpty())
            version = downloaderScript.callAttr("dlp_version").toString()
        Log.v(TAG, version)
        return version
    }

}

// Extension property to convert Kotlin types to Python types
private val Any.asPy get() = PyObject.fromJava(this)
