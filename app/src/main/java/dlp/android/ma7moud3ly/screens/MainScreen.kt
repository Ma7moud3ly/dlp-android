package dlp.android.ma7moud3ly.screens

import dlp.android.ma7moud3ly.screens.home.HomeScreen
import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dlp.android.ma7moud3ly.MainViewModel
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.screens.about.AboutScreen
import dlp.android.ma7moud3ly.screens.downloads.DownloadsScreen
import kotlinx.coroutines.launch

private const val TAG = "TabsScreen"

@Composable
fun MainScreen(viewModel: MainViewModel) {
    val coroutineScope = rememberCoroutineScope()
    val snackarHostState = remember { SnackbarHostState() }
    val pagerState = rememberPagerState(pageCount = { appTabs.size })

    BackHandler {
        val currentPage = pagerState.settledPage
        if (currentPage > 0) {
            coroutineScope.launch {
                pagerState.scrollToPage(currentPage - 1)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarMessage.collect { msg ->
            Log.i(TAG, "snackbarMessage")
            snackarHostState.showSnackbar(msg)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.selectedTabIndex.collect { index ->
            Log.i(TAG, "selectedTabIndex - $index")
            pagerState.scrollToPage(index)
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                onSelectPage = { page ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(page)
                    }
                },
                selectedPage = { pagerState.settledPage }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackarHostState)
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .fillMaxSize()
        ) {
            HorizontalPager(state = pagerState, userScrollEnabled = false) { page ->
                when (page) {
                    0 -> HomeScreen()
                    1 -> DownloadsScreen()
                    2 -> AboutScreen()
                }
            }
        }
    }
}


/**
 * Bottom Bar
 */
@Composable
private fun BottomBar(
    onSelectPage: (Int) -> Unit,
    selectedPage: () -> Int
) {
    val selectedPage = selectedPage()
    Box(Modifier.background(color = MaterialTheme.colorScheme.background)) {
        NavigationBar(
            containerColor = Color.White,
            contentColor = Color.White,
            tonalElevation = 5.dp,
            modifier = Modifier.clip(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomEnd = 0.dp,
                    bottomStart = 0.dp
                )
            )
        ) {
            appTabs.forEach { tab ->
                val title = stringResource(id = tab.title)
                val isSelected = selectedPage == tab.id
                val itemColor = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onSelectPage(tab.id) },
                    label = {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = itemColor
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = tab.icon),
                            contentDescription = title,
                            modifier = Modifier
                                .padding(top = 4.dp, end = 4.dp)
                                .size(30.dp),
                            tint = itemColor
                        )
                    }, colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.secondary,
                        unselectedTextColor = MaterialTheme.colorScheme.secondary
                    )
                )
            }
        }
    }
}


private val appTabs = listOf(
    MyTab(
        id = 0,
        title = R.string.tab_home,
        icon = R.drawable.download
    ),
    MyTab(
        id = 1,
        title = R.string.tab_downloads,
        icon = R.drawable.videos
    ),
    MyTab(
        id = 2,
        title = R.string.tab_about,
        icon = R.drawable.settings
    )
)

data class MyTab(
    val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int
)
