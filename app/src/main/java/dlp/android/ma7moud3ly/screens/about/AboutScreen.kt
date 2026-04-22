package dlp.android.ma7moud3ly.screens.about

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import dlp.android.ma7moud3ly.managers.DownloadManager
import androidx.core.net.toUri


@Composable
fun AboutScreen() {
    val activity = LocalActivity.current as Activity
    val downloadManager = remember { DownloadManager.instance }
    val dlpVersion = remember { downloadManager.dlpVersion() }


    AboutScreenContent(
        dlpVersion = dlpVersion,
        onOpenRepo = {
            try {
                val url = "https://github.com/Ma7moud3ly/dlp-android"
                val browserIntent = Intent(Intent.ACTION_VIEW, url.toUri())
                activity.startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )
}