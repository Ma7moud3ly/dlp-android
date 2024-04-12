package dlp.android.ma7moud3ly.data

import java.io.File

sealed class HomeEvents {
    data class OnOpen(val url: String) : HomeEvents()
    data class OnInfo(val url: String) : HomeEvents()
    data class OnPlay(val format: MediaFormat) : HomeEvents()
    data class OnDownload(val format: MediaFormat) : HomeEvents()
    data object OnDownloadBest : HomeEvents()

    data object OnStopDownload : HomeEvents()
    data object OnClearMedia : HomeEvents()
}

sealed class DownloadsEvents {
    data object DeleteAll : DownloadsEvents()
    data class SaveExternally(val mediaFile: File) : DownloadsEvents()
    data class Share(val mediaFile: File) : DownloadsEvents()
    data class Play(val mediaFile: File) : DownloadsEvents()
    data class Delete(val mediaFile: File) : DownloadsEvents()
}