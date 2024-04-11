import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.data.DownloadProgress
import dlp.android.ma7moud3ly.data.HomeEvents
import dlp.android.ma7moud3ly.data.MediaFormat
import dlp.android.ma7moud3ly.data.MediaInfo
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme
import dlp.android.ma7moud3ly.ui.appTheme.borderColor
import dlp.android.ma7moud3ly.ui.home.dialogs.DownloadDialog
import dlp.android.ma7moud3ly.ui.home.dialogs.ProgressDialog
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
        HomeScreenContent(
            mediaInfo = { mediaInfo },
            isLoading = { false },
            selectedFormat = { null },
            downloadProgress = { DownloadProgress() },
            action = {}
        )
    }
}


@Composable
fun HomeScreenContent(
    isLoading: () -> Boolean,
    mediaInfo: () -> MediaInfo?,
    downloadProgress: () -> DownloadProgress,
    selectedFormat: () -> MediaFormat?,
    action: (HomeEvents) -> Unit
) {
    var expandDetails by remember { mutableStateOf(true) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        SectionVideoUrl(
            mediaInfo = mediaInfo,
            onEnter = { action(HomeEvents.OnInfo(it)) }
        )

        SectionVideoDetails(
            mediaInfo = mediaInfo,
            expandDetails = { expandDetails },
            onClearMedia = { action(HomeEvents.OnClearMedia) },
            onDownloadBest = { action(HomeEvents.OnDownloadBest) },
            onOpenMedia = { action(HomeEvents.OnOpen(it)) },
        )
        Spacer(modifier = Modifier.height(8.dp))
        SectionVideoFormats(
            formats = { mediaInfo()?.formats.orEmpty() },
            onDownload = { action(HomeEvents.OnDownload(it)) },
            onPlay = { action(HomeEvents.OnPlay(it)) },
            onScrollChanges = { expandDetails = it }
        )
    }

    ProgressDialog(show = isLoading)
    DownloadDialog(
        mediaInfo = mediaInfo,
        mediaFormat = selectedFormat,
        downloadProgress = downloadProgress,
        onStopDownload = { action(HomeEvents.OnStopDownload) }
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
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = stringResource(id = R.string.app_name),
                            modifier = Modifier
                                .size(70.dp)
                                .clip(CircleShape)

                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        value = query,
                        singleLine = false,
                        shape = RoundedCornerShape(16.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(
                            onGo = {
                                onEnter(query)
                                focusManager.clearFocus(true)
                            }
                        ),
                        onValueChange = { query = it },
                        placeholder = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = stringResource(id = R.string.download_video_hint),
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = stringResource(id = R.string.download_video_hint),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.secondary,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.tertiary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = borderColor,
                        ),
                        textStyle = MaterialTheme.typography.bodySmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        ButtonSmall(
                            text = stringResource(id = R.string.download_video_find),
                            onClick = { onEnter(query) },
                            enabled = { query.isNotBlank() },
                            background = Color.White,
                            color = MaterialTheme.colorScheme.secondary,
                            border = BorderStroke(1.dp, borderColor),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
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
    onDownloadBest: () -> Unit,
    onClearMedia: () -> Unit
) {
    val info = mediaInfo() ?: return
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.clickable(onClick = onClearMedia)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = stringResource(id = R.string.download_back),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
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
                        contentScale = ContentScale.Crop,
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
                ButtonSmall(
                    text = stringResource(id = R.string.download_best_quality),
                    color = Color.White,
                    background = MaterialTheme.colorScheme.primary,
                    onClick = onDownloadBest,
                    icon = R.drawable.download,
                    height = 40.dp,
                    fillMaxWidth = true
                )
                Text(
                    text = info.title,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenMedia(info.url) },
                    maxLines = 4,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    overflow = TextOverflow.Ellipsis
                )
                if (info.description.isNotEmpty() && expandDetails()) Text(
                    text = info.description,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
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
                if (info.resolution.isNotEmpty()) Text(
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
                    text = stringResource(id = R.string.download),
                    background = Color.Transparent,
                    color = MaterialTheme.colorScheme.secondary,
                    border = BorderStroke(1.dp, borderColor),
                    onClick = onDownload,
                    icon = R.drawable.download
                )
                ButtonSmall(
                    text = stringResource(id = R.string.download_preview),
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
    fillMaxWidth: Boolean = false,
    background: Color = MaterialTheme.colorScheme.primary,
) {
    Surface(
        color = background,
        modifier = Modifier
            .height(height)
            .then(
                if (fillMaxWidth) Modifier.fillMaxSize()
                else Modifier.width(IntrinsicSize.Max)
            )
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
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                textAlign = TextAlign.Center,
                color = color,
                minLines = 1
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