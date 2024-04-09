package dlp.android.ma7moud3ly

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dlp.android.ma7moud3ly.data.DownloadInfo
import dlp.android.ma7moud3ly.data.MediaInfo
import kotlinx.coroutines.flow.MutableSharedFlow

class MainViewModel : ViewModel() {
    val snackbarMessage = MutableSharedFlow<String>(replay = 0)
    val selectedTabIndex = MutableSharedFlow<Int>(replay = 0)
    val downloadList = mutableStateListOf<DownloadInfo>()
    val mediaInfo = mutableStateOf<MediaInfo?>(null)
}