package dlp.android.ma7moud3ly.data

import android.graphics.Bitmap
import java.io.File

data class DownloadProgress(
    val downloaded: Double = 0.0,
    val size: Double = 0.0,
    val percent: Float = 0f
)

/**
 * Downloaded file info in app internal storage
 */
data class DownloadInfo(
    val file: File = File(""),
    val thumbnail: Bitmap? = null
) {
    val size: Double get() = file.length().toInt().toMega
    val name: String get() = file.name
    val ext get() = file.extension
}
