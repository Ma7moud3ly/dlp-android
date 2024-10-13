package dlp.android.ma7moud3ly.screens.downloads.menus

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme

@Preview
@Composable
private fun MediaDropdownMenuPreview() {
    AppTheme {
        DownloadedMediaDropdownMenu(
            expanded = { true },
            canPlay = true
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DownloadedMediaDropdownMenu(
    canPlay: Boolean,
    expanded: () -> Boolean = { false },
    onDismiss: () -> Unit = {},
    onSave: () -> Unit = {},
    onShare: () -> Unit = {},
    onPlay: () -> Unit = {},
    onDelete: () -> Unit = {}
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
                title = R.string.downloads_save_externally,
                icon = R.drawable.ic_public,
                onClick = {
                    onSave()
                    onDismiss()
                }
            )
            ItemOptionMenu(
                title = R.string.downloads_share,
                icon = R.drawable.share,
                onClick = {
                    onShare()
                    onDismiss()
                }
            )
            if (canPlay) ItemOptionMenu(
                title = R.string.downloads_play,
                icon = R.drawable.play,
                onClick = {
                    onPlay()
                    onDismiss()
                }
            )
            ItemOptionMenu(
                title = R.string.downloads_delete,
                icon = R.drawable.delete,
                onClick = {
                    onDelete()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
fun ItemOptionMenu(
    @StringRes title: Int,
    @DrawableRes icon: Int,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "",
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.secondary
        )
        Text(
            text = stringResource(id = title),
            color = MaterialTheme.colorScheme.secondary,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier
                .background(Color.White, shape = RoundedCornerShape(8.dp))
                .weight(1f)
                .wrapContentHeight()
                .padding(vertical = 4.dp, horizontal = 8.dp),
            textAlign = TextAlign.Start
        )

    }
}
