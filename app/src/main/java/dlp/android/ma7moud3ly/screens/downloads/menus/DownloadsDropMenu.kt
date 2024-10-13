package dlp.android.ma7moud3ly.screens.downloads.menus

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.DropdownMenu
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme

@Preview
@Composable
private fun MediaDropdownMenuPreview() {
    AppTheme {
        DownloadsDropdownMenu(
            expanded = { true }
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DownloadsDropdownMenu(
    onDismiss: () -> Unit = {},
    expanded: () -> Boolean = { false },
    onDeleteAll: () -> Unit = {}
) {
    DropdownMenu(
        expanded = expanded(),
        properties = PopupProperties(
            usePlatformDefaultWidth = false
        ),
        modifier = Modifier
            .background(color = Color.White)
            .fillMaxWidth(0.9f)
            .wrapContentHeight(),
        onDismissRequest = onDismiss,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ItemOptionMenu(
                title = R.string.downloads_delete_all,
                icon = R.drawable.delete,
                onClick = {
                    onDeleteAll()
                    onDismiss()
                }
            )
        }
    }
}
