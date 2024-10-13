package dlp.android.ma7moud3ly

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import dlp.android.ma7moud3ly.managers.DownloadManager
import dlp.android.ma7moud3ly.managers.LibraryManager
import dlp.android.ma7moud3ly.screens.TabsScreen
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme


private const val TAG = "MainActivity"

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        initManagers()
        setContent {
            AppTheme {
                TabsScreen()
            }
        }
    }

    // init download manager & library manager
    private fun initManagers() {
        LibraryManager.init(this)
        val downloadsPath = LibraryManager.instance.downloadsPath
        Log.i(TAG, "downloadsPath = $downloadsPath")
        DownloadManager.init(this, downloadsPath)
        LibraryManager.instance.restoreMediaInfo()?.let {
            Log.i(TAG, "restoreMediaInfo - $it")
            viewModel.mediaInfo.value = it
        }
    }
}

