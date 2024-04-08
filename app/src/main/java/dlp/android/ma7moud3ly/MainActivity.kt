package dlp.android.ma7moud3ly

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import dlp.android.ma7moud3ly.DownloadManager.Companion
import dlp.android.ma7moud3ly.ui.HomeScreen
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme
import java.io.File


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init download manager
        initDownloadManager()

        enableEdgeToEdge()
        setContent {
            AppTheme {
                HomeScreen()
            }
        }
    }

    private fun initDownloadManager() {
        val downloadsPath = initExternalStorage()
        Log.i(TAG, "downloadsPath = $downloadsPath")
        DownloadManager.init(this, downloadsPath)
        DownloadManager.instance.restoreMediaInfo()?.let {
            Log.i(TAG, "restoreMediaInfo - $it")
            viewModel.mediaInfo.value = it
        }
    }

    /**
     * Return app external media storage path
     * /storage/emulated/0/Android/media/<package_name>/Downloads
     */
    private fun initExternalStorage(): String {
        val mediaDir = externalMediaDirs.getOrNull(0) ?: return ""
        val downloadsDir = File(mediaDir, "Downloads")
        downloadsDir.deleteRecursively()
        if (downloadsDir.exists().not()) downloadsDir.mkdirs()
        return downloadsDir.path
    }

}

