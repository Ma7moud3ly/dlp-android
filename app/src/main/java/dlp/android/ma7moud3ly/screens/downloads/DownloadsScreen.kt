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
import dlp.android.ma7moud3ly.data.DownloadsEvents
import dlp.android.ma7moud3ly.managers.LibraryManager
import dlp.android.ma7moud3ly.screens.downloads.menus.DeleteConfirmDialog
import kotlinx.coroutines.launch
import java.io.File

private const val TAG = "DownloadsScreen"

@Composable
fun DownloadsScreen() {
    val activity = LocalContext.current as MainActivity
    val viewModel: MainViewModel = viewModel(activity)
    var showFileDelete by remember { mutableStateOf(false) }
    var selectedFile by remember { mutableStateOf<File?>(null) }
    val downloadList = remember { viewModel.downloadList }
    val libraryManager = remember { LibraryManager.instance }
    val coroutineScope = rememberCoroutineScope()

    fun updateMediaList() {
        coroutineScope.launch {
            val downloadedFiles = libraryManager.listDownloadedFiles()
            downloadList.clear()
            downloadList.addAll(downloadedFiles)
        }
    }

    LaunchedEffect(Unit) {
        if (downloadList.isEmpty()) updateMediaList()
    }

    DeleteConfirmDialog(
        file = { selectedFile!! },
        show = { showFileDelete && selectedFile != null },
        onDismiss = { showFileDelete = false },
        onApprove = { if (selectedFile?.delete() == true) updateMediaList() }
    )

    fun events(it: DownloadsEvents) {
        when (it) {
            is DownloadsEvents.DeleteAll -> {
                libraryManager.deleteAllFiles()
                updateMediaList()
            }

            is DownloadsEvents.SaveExternally -> {
                val file = it.mediaFile
                coroutineScope.launch {
                    val moved = libraryManager.moveMediaFileToPublicDownloads(file)
                    if (moved) {
                        updateMediaList()
                        viewModel.snackbarMessage.emit(
                            activity.getString(
                                R.string.downloads_saved_externally,
                                file.name
                            )
                        )
                    }
                }
            }

            is DownloadsEvents.Share -> {
                libraryManager.shareMediaFile(it.mediaFile)
            }

            is DownloadsEvents.Play -> {
                libraryManager.playMediaFile(it.mediaFile)
            }

            is DownloadsEvents.Delete -> {
                selectedFile = it.mediaFile
                showFileDelete = true
            }
        }
    }

    DownloadsScreenContent(
        downloads = downloadList,
        action = ::events
    )
}

