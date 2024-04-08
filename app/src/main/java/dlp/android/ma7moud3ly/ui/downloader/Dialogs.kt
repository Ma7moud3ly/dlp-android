package dlp.android.ma7moud3ly.ui.downloader

import ButtonSmall
import android.util.Log
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import dlp.android.ma7moud3ly.DownloadProgress
import dlp.android.ma7moud3ly.MediaFormat
import dlp.android.ma7moud3ly.MediaInfo
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val TAG = "DownloadProgressDialog"

@Preview
@Composable
private fun DownloadProgressDialogPreview() {

    val format = MediaFormat(
        resolution = "256x144",
        ext = "MP4",
        fileSize = "0" //"8388608"
    )
    val mediaInfo = MediaInfo(title = "My Test Video")
    var progress by remember { mutableStateOf(DownloadProgress()) }
    LaunchedEffect(Unit) {
        for (i in 1..100) {
            delay(1000)
            val downloaded = 1.0 * i
            val total = 30.0
            //val percent = "%.2f".format(downloaded / total * 100).toFloatOrNull() ?: 0f
            val percent = 0f
            progress = DownloadProgress(
                downloaded = downloaded,
                size = 0.0,
                percent = percent
            )
        }
    }

    AppTheme {

        Surface(color = Color.White) {
            DownloadProgressDialog(
                mediaFormat = { format },
                mediaInfo = { mediaInfo },
                downloadProgress = { progress },
                onStopDownload = {}
            )
        }
    }
}

@Composable
fun DownloadProgressDialog(
    mediaFormat: () -> MediaFormat?,
    mediaInfo: () -> MediaInfo?,
    downloadProgress: () -> DownloadProgress,
    onStopDownload: () -> Unit
) {
    val format = mediaFormat() ?: return
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false
        )
    ) {
        val coroutineScope = rememberCoroutineScope()
        val progress = downloadProgress()
        val info = mediaInfo()
        var progressJob by remember { mutableStateOf<Job?>(null) }
        var progressPercent by remember {
            mutableFloatStateOf(progress.percent / 100)
        }

        Surface(color = Color.White) {
            Column {
                LinearProgressIndicator(
                    progress = { progressPercent },
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.tertiary,
                    trackColor = MaterialTheme.colorScheme.primary,
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(16.dp)
                ) {
                    val percent1 = if (progress.percent == 0f) ""
                    else "${progress.percent.toInt()}% "

                    Text(
                        text = "$percent1${info?.title.orEmpty()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val size = if (progress.size > 0) progress.size
                    else format.size() ?: 0.0
                    if (size > 0.0) FormatDetails(
                        title = R.string.download_progress_size,
                        value = "$size MB"
                    )
                    val percent2 = if (progress.percent == 0f) ""
                    else "(${progress.percent}%)"
                    if (progress.downloaded > 0.0) FormatDetails(
                        title = R.string.download_progress,
                        value = "${progress.downloaded} MB $percent2"
                    )
                    FormatDetails(
                        title = R.string.download_progress_format,
                        value = format.ext + " - " + format.formatNote
                    )
                    FormatDetails(
                        title = R.string.download_progress_resolution,
                        value = format.resolution
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ButtonSmall(
                        text = stringResource(id = R.string.download_stop),
                        background = MaterialTheme.colorScheme.primary,
                        onClick = onStopDownload
                    )
                }
            }
        }

        LaunchedEffect(progress.size) {
            Log.i(TAG, "progress size: ${progress.size}")
            if (progress.size == 0.0 && progressJob == null) {
                progressJob = coroutineScope.launch {
                    while (true) {
                        for (i in 0..10) {
                            progressPercent = i / 10f
                            delay(100)
                            //Log.v(TAG, "progress percent: $progressPercent")
                        }
                        delay(200)
                    }
                }
            } else {
                progressJob?.cancel()
                progressJob = null
            }
        }

        LaunchedEffect(progress.percent) {
            if (progressJob == null && progress.percent > 0.0) {
                //Log.i(TAG, "progress percent: $progressPercent")
                progressPercent = progress.percent / 100
            }
        }
    }
}

@Composable
private fun FormatDetails(@StringRes title: Int, value: Any) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(0.4f)
        )
        Text(
            text = value.toString(),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.weight(0.6f),
            maxLines = 1
        )
    }
}


@Composable
fun LoadingDialog(
    show: () -> Boolean,
    background: Color = Color.Transparent,
    dismissOnBackPress: Boolean = false,
    dismissOnClickOutside: Boolean = false
) {
    if (show()) Dialog(
        onDismissRequest = {},
        properties = DialogProperties(
            dismissOnBackPress = dismissOnBackPress,
            dismissOnClickOutside = dismissOnClickOutside
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .background(color = background)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp),
                color = MaterialTheme.colorScheme.tertiary
            )
        }

    }
}