package dlp.android.ma7moud3ly.data

import com.google.gson.annotations.SerializedName

val Int.toMega get() = "%.2f".format(this / 1024.0 / 1024.0).toDoubleOrNull() ?: 0.0

/**
 * Details about a video/audio extracted by yt-dlp
 */
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
