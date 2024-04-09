package dlp.android.ma7moud3ly.data

import java.io.File

sealed class DownloadEvents {
    data class OnOpen(val url: String) : DownloadEvents()
    data class OnInfo(val url: String) : DownloadEvents()
    data class OnPlay(val format: MediaFormat) : DownloadEvents()
    data class OnDownload(val format: MediaFormat) : DownloadEvents()
    data object OnStopDownload : DownloadEvents()
    data object OnClearMedia : DownloadEvents()
}

sealed class LibraryEvents {
    data object DeleteAll : LibraryEvents()
    data class SaveExternally(val mediaFile: File) : LibraryEvents()
    data class Share(val mediaFile: File) : LibraryEvents()
    data class Play(val mediaFile: File) : LibraryEvents()
    data class Delete(val mediaFile: File) : LibraryEvents()
}