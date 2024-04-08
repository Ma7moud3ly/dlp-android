package dlp.android.ma7moud3ly

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val mediaUrl = mutableStateOf("")
    val mediaInfo = mutableStateOf<MediaInfo?>(null)
}