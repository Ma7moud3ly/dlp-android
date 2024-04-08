import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import dlp.android.ma7moud3ly.DownloadEvents
import dlp.android.ma7moud3ly.DownloadProgress
import dlp.android.ma7moud3ly.MediaFormat
import dlp.android.ma7moud3ly.MediaInfo
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme
import dlp.android.ma7moud3ly.ui.appTheme.borderColor
import dlp.android.ma7moud3ly.ui.downloader.DownloadProgressDialog
import dlp.android.ma7moud3ly.ui.downloader.LoadingDialog
import java.util.Locale

@Preview(showSystemUi = true)
@Composable
private fun DownloaderScreenPreview() {
    val formats = listOf(
        MediaFormat(
            ext = "mp4",
            resolution = "480*360",
            fileSize = "50000"
        ),
        MediaFormat(
            ext = "mp4",
            resolution = "480*360",
            fileSize = "800000"
        ),
        MediaFormat(
            ext = "webm",
            resolution = "480*360",
            fileSize = "500000"
        )
    )

    val mediaInfo = MediaInfo(
        title = "Test video",
        url = "https://www.google.com",
        formats = formats,
        duration = 120,
        description = "test video description"
    )
    AppTheme {
        DownloaderScreenContent(
            mediaInfo = { mediaInfo },
            isLoading = { false },
            selectedFormat = { null },
            downloadProgress = { DownloadProgress() },
            action = {}
        )
    }
}


@Composable
fun DownloaderScreenContent(
    isLoading: () -> Boolean,
    mediaInfo: () -> MediaInfo?,
    downloadProgress: () -> DownloadProgress,
    selectedFormat: () -> MediaFormat?,
    action: (DownloadEvents) -> Unit
) {
    var expandDetails by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionVideoUrl(
            mediaInfo = mediaInfo,
            onEnter = { action(DownloadEvents.OnInfo(it)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
        SectionVideoDetails(
            mediaInfo = mediaInfo,
            expandDetails = { expandDetails },
            onClearMedia = { action(DownloadEvents.OnClearMedia) },
            onOpenMedia = { action(DownloadEvents.OnOpen(it)) }
        )
        Spacer(modifier = Modifier.height(8.dp))
        SectionVideoFormats(
            formats = { mediaInfo()?.formats.orEmpty() },
            onDownload = { action(DownloadEvents.OnDownload(it)) },
            onPlay = { action(DownloadEvents.OnPlay(it)) },
            onScrollChanges = { expandDetails = it }
        )
    }

    LoadingDialog(show = isLoading)
    DownloadProgressDialog(
        mediaInfo = mediaInfo,
        mediaFormat = selectedFormat,
        downloadProgress = downloadProgress,
        onStopDownload = { action(DownloadEvents.OnStopDownload) }
    )
}


@Composable
private fun SectionVideoUrl(
    mediaInfo: () -> MediaInfo?,
    onEnter: (String) -> Unit
) {
    AnimatedVisibility(visible = mediaInfo() == null) {
        var query by remember { mutableStateOf("") }
        val focusRequester = remember { FocusRequester() }
        val focusManager = LocalFocusManager.current

        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.align(Alignment.Center),
                border = BorderStroke(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape),
                        )
                        Text(
                            text = stringResource(id = R.string.app_name),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            textAlign = TextAlign.Start,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, borderColor),
                    ) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = stringResource(id = R.string.download_video_hint),
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(30.dp)
                        )
                        OutlinedTextField(
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester),
                            value = query,
                            singleLine = false,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    onEnter(query)
                                    focusManager.clearFocus(true)
                                }
                            ),
                            onValueChange = { query = it },
                            placeholder = {
                                Text(
                                    text = stringResource(id = R.string.download_video_hint),
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = MaterialTheme.colorScheme.secondary,
                                focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                            ),
                            textStyle = MaterialTheme.typography.bodyLarge
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ButtonSmall(
                            text = stringResource(id = R.string.download_video_find),
                            onClick = { onEnter(query) },
                            enabled = { query.isNotBlank() }
                        )
                        ButtonSmall(
                            text = stringResource(id = R.string.download_video_clear),
                            color = MaterialTheme.colorScheme.secondary,
                            background = Color.White,
                            border = BorderStroke(1.dp, borderColor),
                            onClick = { query = "" }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionVideoDetails(
    expandDetails: () -> Boolean,
    mediaInfo: () -> MediaInfo?,
    onOpenMedia: (String) -> Unit,
    onClearMedia: () -> Unit
) {
    val info = mediaInfo() ?: return
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.close),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier
                    .clickable(onClick = onClearMedia)
                    .size(24.dp)
            )
            Text(
                text = info.url,
                color = MaterialTheme.colorScheme.secondary,
                textDecoration = TextDecoration.Underline,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.clickable { onOpenMedia(info.url) }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
            border = BorderStroke(1.dp, borderColor),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(16.dp)
            ) {
                AnimatedVisibility(visible = expandDetails()) {
                    Image(
                        contentScale = ContentScale.Fit,
                        painter = rememberAsyncImagePainter(
                            model = info.thumbnail,
                            placeholder = painterResource(R.drawable.logo)
                        ),
                        contentDescription = info.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
                Text(
                    text = info.title,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis
                )
                if (info.description.isNotEmpty() && expandDetails()) Text(
                    text = info.description,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Light,
                )
            }
        }
    }
}

@Composable
private fun SectionVideoFormats(
    formats: () -> List<MediaFormat>,
    onDownload: (MediaFormat) -> Unit,
    onPlay: (MediaFormat) -> Unit,
    onScrollChanges: (onTop: Boolean) -> Unit
) {
    val lazyListState = rememberLazyListState()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = lazyListState
    ) {
        val list = formats()
        items(list.size) { index ->
            val formatInfo = list[index]
            ItemVideoFormat(
                info = formatInfo,
                onDownload = { onDownload(formatInfo) },
                onPlay = { onPlay(formatInfo) }
            )
            VerticalDivider()
        }
    }

    LaunchedEffect(lazyListState.canScrollBackward) {
        onScrollChanges(lazyListState.firstVisibleItemScrollOffset == 0)
    }
}

@Composable
private fun ItemVideoFormat(
    info: MediaFormat,
    onPlay: () -> Unit,
    onDownload: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth(),
        border = BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row {
                Text(
                    text = info.ext.uppercase(Locale.getDefault()),
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = " - ${info.resolution}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                val size = info.size()
                if (size != null) Text(
                    text = "$size MB",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ButtonSmall(
                    text = stringResource(id = R.string.download_video),
                    background = Color.Transparent,
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, borderColor),
                    onClick = onDownload,
                    icon = R.drawable.download
                )
                ButtonSmall(
                    text = stringResource(id = R.string.download_video_play),
                    background = Color.Transparent,
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, borderColor),
                    onClick = onPlay,
                    icon = R.drawable.play
                )
            }
        }
    }
}

@Composable
fun ButtonSmall(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int? = null,
    color: Color = Color.White,
    border: BorderStroke? = null,
    height: Dp = 30.dp,
    enabled: () -> Boolean = { true },
    background: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        color = background,
        modifier = Modifier
            .height(height)
            .wrapContentWidth()
            .then(modifier),
        shape = RoundedCornerShape(8.dp),
        border = border,
        onClick = onClick,
        enabled = enabled()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 4.dp,
                vertical = 4.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp),
                textAlign = TextAlign.Center,
                color = color
            )
            icon?.let {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = "",
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}