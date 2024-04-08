package dlp.android.ma7moud3ly

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.google.gson.annotations.SerializedName

val Int.toMega get() = "%.2f".format(this / 1024.0 / 1024.0).toDoubleOrNull() ?: 0.0

data class DownloadProgress(
    val downloaded: Double = 0.0,
    val size: Double = 0.0,
    val percent: Float = 0f
)

data class MediaInfo(
    @SerializedName("url") val url: String = "",
    @SerializedName("title") val title: String = "",
    @SerializedName("thumbnail") val thumbnail: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("duration") val duration: Int = 0,
    @SerializedName("formats") val formats: List<MediaFormat> = listOf()
)

data class MediaFormat(
    @SerializedName("format_id") val formatId: String = "",
    @SerializedName("format_note") val formatNote: String = "",
    @SerializedName("resolution") val resolution: String = "",
    @SerializedName("file_size") val fileSize: String = "",
    @SerializedName("media_link") val mediaLink: String = "",
    @SerializedName("ext") val ext: String = ""
) {
    fun size(): Double? {
        val size = fileSize.toIntOrNull() ?: return null
        return size.toMega
    }
}


data class MyTab(
    val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screen: @Composable (SnackbarHostState) -> Unit
)

sealed class DownloadEvents {
    data class OnOpen(val url: String) : DownloadEvents()
    data class OnInfo(val url: String) : DownloadEvents()
    data class OnPlay(val format: MediaFormat) : DownloadEvents()
    data class OnDownload(val format: MediaFormat) : DownloadEvents()
    data object OnStopDownload : DownloadEvents()
    data object OnClearMedia : DownloadEvents()
}