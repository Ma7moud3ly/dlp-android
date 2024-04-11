import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import dlp.android.ma7moud3ly.managers.DownloadManager


@Composable
fun AboutScreen() {
    val activity = LocalContext.current as Activity
    val downloadManager = remember { DownloadManager.instance }
    val dlpVersion = remember { downloadManager.dlpVersion() }


    AboutScreenContent(
        dlpVersion = dlpVersion,
        onOpenRepo = {
            try {
                val url = "https://github.com/Ma7moud3ly/dlp-android"
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                activity.startActivity(browserIntent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    )
}