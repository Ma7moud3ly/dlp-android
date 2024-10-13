import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dlp.android.ma7moud3ly.MainActivity
import dlp.android.ma7moud3ly.MainViewModel
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.data.HomeEvents
import dlp.android.ma7moud3ly.data.DownloadProgress
import dlp.android.ma7moud3ly.data.MediaFormat
import dlp.android.ma7moud3ly.managers.DownloadManager
import dlp.android.ma7moud3ly.managers.LibraryManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val TAG = "HomeScreen"

@Composable
fun HomeScreen() {
    val activity = LocalContext.current as MainActivity
    val viewModel: MainViewModel = viewModel(activity)
    val coroutineScope = rememberCoroutineScope()
    val downloadManager = remember { DownloadManager.instance }

    var isLoading by remember { mutableStateOf(false) }
    var mediaInfo by remember { viewModel.mediaInfo }
    var downloadProgress by remember { mutableStateOf(DownloadProgress()) }
    var selectedFormat by remember { mutableStateOf<MediaFormat?>(null) }

    LaunchedEffect(Unit) {
        downloadManager.downloadProgressFlow.collect {
            if (it != null) downloadProgress = it
        }
    }

    LaunchedEffect(Unit) {
        downloadManager.errorFlow.collect { msg ->
            Log.e(TAG, "errorFlow - $msg")
            isLoading = false
            delay(500)
            viewModel.snackbarMessage.emit(msg)
        }
    }

    LaunchedEffect(Unit) {
        downloadManager.downloadCompleteFlow.collect { done ->
            if (done != true) return@collect
            Log.v(TAG, "downloadCompleteFlow..")
            selectedFormat = null
            with(viewModel) {
                snackbarMessage.emit(activity.getString(R.string.download_completed))
                downloadList.clear()
                selectedTabIndex.emit(1)
            }
            downloadManager.downloadCompleteFlow.emit(null)
        }
    }

    fun downloadMedia(format: MediaFormat, bestQuality: Boolean = false) {
        Log.i(TAG, "downloadMedia - ${format.formatId}")
        selectedFormat = format
        downloadProgress = DownloadProgress()
        coroutineScope.launch {
            downloadManager.download(
                url = mediaInfo?.url.orEmpty(),
                title = mediaInfo?.title.orEmpty(),
                formatId = if (bestQuality) "" else format.formatId
            )
        }
    }


    val action: (HomeEvents) -> Unit = {
        when (it) {
            is HomeEvents.OnOpen -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(it.url)
                }
                activity.startActivity(intent)
            }

            is HomeEvents.OnInfo -> {
                val url = it.url
                Log.i(TAG, "OnInfo - $url")
                isLoading = true
                coroutineScope.launch {
                    mediaInfo = downloadManager.getMediaInfo(url)
                    mediaInfo?.let { info ->
                        LibraryManager.instance.saveMediaInfo(info)
                    }
                    isLoading = false
                }
            }

            is HomeEvents.OnDownload -> {
                downloadMedia(it.format)
            }

            is HomeEvents.OnDownloadBest -> {
                mediaInfo?.formats?.getOrNull(0)?.let { bestFormat ->
                    downloadMedia(format = bestFormat, bestQuality = true)
                }
            }


            is HomeEvents.OnStopDownload -> {
                coroutineScope.launch {
                    downloadManager.terminateExecution()
                    Log.v(TAG, "onDownloadCancelled..")
                    selectedFormat = null
                }
                coroutineScope.launch {
                    delay(1000)
                    viewModel.snackbarMessage.emit(activity.getString(R.string.download_stopped))
                }
            }

            is HomeEvents.OnPlay -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(it.format.mediaLink)
                }
                activity.startActivity(intent)
            }

            is HomeEvents.OnClearMedia -> {
                selectedFormat = null
                mediaInfo = null
                LibraryManager.instance.clearMediaInfo()
            }
        }
    }

    HomeScreenContent(
        isLoading = { isLoading },
        mediaInfo = { mediaInfo },
        selectedFormat = { selectedFormat },
        downloadProgress = { downloadProgress },
        action = action
    )
}
