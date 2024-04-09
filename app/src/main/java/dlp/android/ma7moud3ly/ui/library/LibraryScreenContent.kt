import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.data.DownloadInfo
import dlp.android.ma7moud3ly.data.LibraryEvents
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme
import dlp.android.ma7moud3ly.ui.appTheme.borderColor
import dlp.android.ma7moud3ly.ui.library.menus.DownloadedMediaDropdownMenu
import dlp.android.ma7moud3ly.ui.library.menus.LibraryDropdownMenu
import java.io.File

private const val TAG = "VideosScreen"

@Preview
@Composable
private fun VideosScreenPreview() {
    val downloads = listOf(
        DownloadInfo(file = File("Video 1.mp4")),
        DownloadInfo(file = File("Video 2.mp4")),
        DownloadInfo(file = File("Video 3.mp4"))
    )
    AppTheme {
        LibraryScreenContent(
            downloads = downloads,
            action = {}
        )
    }
}


@Composable
fun LibraryScreenContent(
    downloads: List<DownloadInfo>,
    action: (LibraryEvents) -> Unit
) {
    Surface(color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SectionHeader(action)
            if (downloads.isNotEmpty()) LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(downloads.size) { index ->
                    val download = downloads[index]
                    ItemVideo(download, action)
                }
            } else SectionNoDownloads()
        }
    }
}

@Composable
private fun SectionHeader(action: (LibraryEvents) -> Unit) {
    var showOptionsMenu by remember { mutableStateOf(false) }
    Surface(color = Color.White) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(id = R.string.library_downloads),
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )
            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showOptionsMenu = showOptionsMenu.not() }
                )
                LibraryDropdownMenu(
                    expanded = { showOptionsMenu },
                    onDismiss = { showOptionsMenu = false },
                    onDeleteAll = { action(LibraryEvents.DeleteAll) },
                )
            }
        }
    }
}


@Composable
private fun ItemVideo(
    downloadInfo: DownloadInfo,
    action: (LibraryEvents) -> Unit
) {
    var showOptionsMenu by remember { mutableStateOf(false) }

    Surface(
        color = Color.White,
        onClick = { showOptionsMenu = showOptionsMenu.not() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val modifier = Modifier
                .width(90.dp)
                .height(120.dp)
            if (downloadInfo.thumbnail != null) Image(
                bitmap = downloadInfo.thumbnail.asImageBitmap(),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = modifier
            ) else Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = modifier
            )
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = downloadInfo.name,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleMedium,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = borderColor,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = downloadInfo.ext,
                            color = MaterialTheme.colorScheme.secondary,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${downloadInfo.size} MB",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            Box {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { showOptionsMenu = showOptionsMenu.not() }
                )
                DownloadedMediaDropdownMenu(
                    expanded = { showOptionsMenu },
                    onDismiss = { showOptionsMenu = false },
                    onSave = { action(LibraryEvents.SaveExternally(downloadInfo.file)) },
                    onShare = { action(LibraryEvents.Share(downloadInfo.file)) },
                    onPlay = { action(LibraryEvents.Play(downloadInfo.file)) },
                    onDelete = { action(LibraryEvents.Delete(downloadInfo.file)) }
                )
            }
        }
    }
}

@Composable
private fun SectionNoDownloads() {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = stringResource(id = R.string.library_empty),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Light,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}