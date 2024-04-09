package dlp.android.ma7moud3ly.managers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.util.Size
import androidx.core.content.FileProvider
import com.google.gson.Gson
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.data.DownloadInfo
import dlp.android.ma7moud3ly.data.MediaInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Manages downloads functionalities such as saving and restoring media information,
 * listing downloaded files, deleting files, sharing files, playing media files,
 * and moving media files to public downloads.
 * @property downloadsPath The path where downloaded media files are stored.
 * @property activity The activity context.
 */
class LibraryManager private constructor(
    val downloadsPath: String,
    private val activity: Activity
) {
    // File to cache last video information
    private val cacheFile = File(File(downloadsPath).parentFile, "media_info.json")

    companion object {
        private const val TAG = "LibraryManager"

        /**
         * Initializes external storage path for the app.
         * @param activity The activity context.
         * @return The path of the external storage directory.
         */
        private fun initExternalStorage(activity: Activity): String {
            val mediaDir = activity.externalMediaDirs.getOrNull(0) ?: return ""
            val downloadsDir = File(mediaDir, "Downloads")
            if (downloadsDir.exists().not()) downloadsDir.mkdirs()
            return downloadsDir.path
        }

        @SuppressLint("StaticFieldLeak")
        lateinit var instance: LibraryManager
            private set

        /**
         * Initializes a static LibraryManager instance.
         * @param activity The activity context.
         */
        fun init(activity: Activity) {
            val path = initExternalStorage(activity)
            instance = LibraryManager(
                downloadsPath = path,
                activity = activity
            )
        }
    }

    /**
     * Saves media information to a file at [cacheFile] path
     *
     * cacheFile is at Android/Media/<package name>/media_info.json
     * @param mediaInfo The media information to be saved.
     */
    fun saveMediaInfo(mediaInfo: MediaInfo) {
        try {
            val json = Gson().toJson(mediaInfo)
            val outputStream = FileOutputStream(cacheFile)
            outputStream.write(json.toByteArray())
            outputStream.close()
        } catch (e: IOException) {
            Log.e(TAG, "File write failed: $e")
            e.printStackTrace()
        }
    }

    /**
     * Restores media information from cache file.
     * @return The restored media information or null.
     */
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

    /**
     * Clears the cached media information.
     */
    fun clearMediaInfo() {
        if (cacheFile.exists()) cacheFile.delete()
    }

    /**
     * Lists downloaded media files.
     * @return A list of [DownloadInfo] objects representing downloaded media files.
     */
    suspend fun listDownloadedFiles(): List<DownloadInfo> = withContext(Dispatchers.IO) {
        return@withContext File(downloadsPath)
            .listFiles().orEmpty() // list files in app external storage
            // filter downloaded media files by extensions .mp4, .webm, .m4a etc..
            .filter { file -> mediaExt.any { file.name.endsWith(it) } }
            // convert list to DownloadInfo list to display in LibraryScreen
            .map { f ->
                DownloadInfo(file = f, thumbnail = getVideoThumbnail(f.path))
            }
    }

    /**
     * Deletes all downloaded files.
     */
    fun deleteAllFiles() {
        File(downloadsPath).apply {
            deleteRecursively()
            mkdirs()
        }
    }

    /**
     * Shares a media file.
     * @param mediaFile The media file to be shared.
     */
    fun shareMediaFile(mediaFile: File) {
        val shareIntent = createShareIntentForFile(mediaFile, Intent.ACTION_SEND)
        activity.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    /**
     * Plays a media file.
     * @param mediaFile The media file to be played.
     */
    fun playMediaFile(mediaFile: File) {
        val shareIntent = createShareIntentForFile(mediaFile, Intent.ACTION_VIEW)
        try {
            activity.startActivity(shareIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Moves a media file to public downloads.
     * @param medialFile The media file to be moved.
     * @return True if the file is moved successfully, false otherwise.
     */
    suspend fun moveMediaFileToPublicDownloads(medialFile: File): Boolean =
        withContext(Dispatchers.IO) {
            val fileName = medialFile.name
            val values = ContentValues().apply {
                val folderName = Environment.DIRECTORY_DOWNLOADS
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.TITLE, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "video/*|audio/*")
                if (Build.VERSION.SDK_INT >= 29) {
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        folderName + "/${activity.getString(R.string.app_name)}"
                    )
                    put(
                        MediaStore.MediaColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000
                    )
                    put(MediaStore.MediaColumns.IS_PENDING, 1)

                } else {
                    put(
                        MediaStore.MediaColumns.DATE_ADDED,
                        System.currentTimeMillis() / 1000
                    )

                }
            }
            val context = activity.applicationContext
            val fileUri = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"), values
            ) ?: return@withContext false

            context.contentResolver.openFileDescriptor(fileUri, "w")?.use { descriptor ->
                try {
                    FileOutputStream(descriptor.fileDescriptor).use { out ->
                        val mediaFile = File(medialFile.path)
                        FileInputStream(mediaFile).use { inputStream ->
                            val buf = ByteArray(8192)
                            while (true) {
                                val sz = inputStream.read(buf)
                                if (sz <= 0) break
                                out.write(buf, 0, sz)
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.e(TAG, e.message.orEmpty())
                    return@withContext false
                }
            }

            values.clear()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) values.put(
                MediaStore.MediaColumns.IS_PENDING,
                0
            )
            try {
                context.contentResolver.update(fileUri, values, null, null)
                Log.i(TAG, "Media file saved to Download")
                medialFile.delete()
                return@withContext true
            } catch (e: Exception) {
                Log.e(TAG, e.message.orEmpty())
                return@withContext false
            }
        }

    /**
     * Private Methods
     */


    /**
     * Creates a share intent for the given file.
     * @param file The file to be shared.
     * @param action The action to be performed (e.g., ACTION_SEND or ACTION_VIEW).
     * @return The created Intent for sharing or viewing the file.
     */
    private fun createShareIntentForFile(file: File, action: String): Intent {
        val context = activity.applicationContext
        // Get the URI of the file using FileProvider
        val fileUri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
        // Create a Share Intent
        val type = "video/*|audio/*"
        val shareIntent = Intent(action).apply {
            if (action == Intent.ACTION_SEND) {
                putExtra(Intent.EXTRA_STREAM, fileUri)
                setType(type)
            } else if (action == Intent.ACTION_VIEW) {
                setDataAndType(fileUri, type);
            }
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        return shareIntent
    }

    /**
     * Retrieves the thumbnail for a video file.
     * @param filePath The path of the video file.
     * @return The thumbnail bitmap if available, otherwise null.
     */
    private fun getVideoThumbnail(filePath: String): Bitmap? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ThumbnailUtils.createVideoThumbnail(
                    File(filePath),
                    Size(120, 120),
                    null
                )
            } else {
                ThumbnailUtils.createVideoThumbnail(
                    filePath,
                    MediaStore.Images.Thumbnails.MINI_KIND
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, e.message.orEmpty())
            null
        }
    }

    // Extension property to check if a file name represents an audio file
    private val String.isAudio: Boolean get() = this.endsWith(".mp3")

    // Array of supported media extensions
    private val mediaExt = arrayOf("mp4", "webm", "mp3", "mkv", "avi", "m4a")

}