import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import dlp.android.ma7moud3ly.DownloadEvents
import dlp.android.ma7moud3ly.DownloadManager
import dlp.android.ma7moud3ly.DownloadProgress
import dlp.android.ma7moud3ly.MainActivity
import dlp.android.ma7moud3ly.MainViewModel
import dlp.android.ma7moud3ly.MediaFormat
import dlp.android.ma7moud3ly.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


private const val TAG = "DownloaderScreen"

@Composable
fun DownloaderScreen(snackbarHostState: SnackbarHostState) {
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
            snackbarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(Unit) {
        downloadManager.downloadCompleteFlow.collect {
            Log.v(TAG, "downloadCompleteFlow..")
            selectedFormat = null
            snackbarHostState.showSnackbar(activity.getString(R.string.download_completed))
        }
    }

    fun downloadMedia(format: MediaFormat) {
        Log.i(TAG, "downloadMedia - ${format.formatId}")
        selectedFormat = format
        downloadProgress = DownloadProgress()
        coroutineScope.launch {
            downloadManager.download(
                url = mediaInfo?.url.orEmpty(),
                formatId = format.formatId
            )
        }
    }


    val action: (DownloadEvents) -> Unit = {
        when (it) {
            is DownloadEvents.OnOpen -> {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(it.url)
                }
                activity.startActivity(intent)
            }

            is DownloadEvents.OnInfo -> {
                val url = it.url
                Log.i(TAG, "OnInfo - $url")
                isLoading = true
                coroutineScope.launch {
                    mediaInfo = downloadManager.getMediaInfo(url)
                    isLoading = false
                }
            }

            is DownloadEvents.OnDownload -> {
                downloadMedia(it.format)
            }

            is DownloadEvents.OnStopDownload -> {
                coroutineScope.launch {
                    downloadManager.terminateExecution()
                    Log.v(TAG, "onDownloadCancelled..")
                    selectedFormat = null
                }
                coroutineScope.launch {
                    delay(1000)
                    snackbarHostState.showSnackbar(activity.getString(R.string.download_stopped))
                }
            }

            is DownloadEvents.OnPlay -> {

            }

            is DownloadEvents.OnClearMedia -> {
                selectedFormat = null
                mediaInfo = null
                downloadManager.clearMediaInfo()
            }
        }
    }

    DownloaderScreenContent(
        isLoading = { isLoading },
        mediaInfo = { mediaInfo },
        selectedFormat = { selectedFormat },
        downloadProgress = { downloadProgress },
        action = action
    )
}

