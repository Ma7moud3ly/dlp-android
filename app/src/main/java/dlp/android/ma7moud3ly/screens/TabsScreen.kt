package dlp.android.ma7moud3ly.screens

import HomeScreen
import AboutScreen
import DownloadsScreen
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import dlp.android.ma7moud3ly.MainActivity
import dlp.android.ma7moud3ly.MainViewModel
import dlp.android.ma7moud3ly.R
import dlp.android.ma7moud3ly.ui.appTheme.AppTheme

private const val TAG = "TabsScreen"

@Preview
@Composable
private fun HomeScreenPreview() {
    AppTheme {
        TabsScreen()
    }
}

@Composable
fun TabsScreen() {
    val activity = LocalContext.current as MainActivity
    val viewModel: MainViewModel = viewModel(activity)
    val snackarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(appTabs[0]) }
    val pagerState = rememberPagerState(pageCount = { appTabs.size })

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

    LaunchedEffect(pagerState.settledPage) {
        selectedTab = when (pagerState.settledPage) {
            0 -> appTabs[0]
            1 -> appTabs[1]
            else -> appTabs[2]
        }
    }

    Scaffold(
        bottomBar = {
            BottomBar(
                onTabSelected = { selectedTab = it },
                selectedTab = { selectedTab }
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
            HorizontalPager(state = pagerState, userScrollEnabled = false) {
                selectedTab.screen()
            }
        }
    }
}


/**
 * Bottom Bar
 */
@Composable
private fun BottomBar(
    onTabSelected: (MyTab) -> Unit,
    selectedTab: () -> MyTab
) {
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
                val isSelected = selectedTab().id == tab.id
                val itemColor = if (isSelected) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.secondary
                NavigationBarItem(
                    selected = isSelected,
                    onClick = { onTabSelected(tab) },
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
        icon = R.drawable.download,
        screen = { HomeScreen() }
    ),
    MyTab(
        id = 1,
        title = R.string.tab_downloads,
        icon = R.drawable.videos,
        screen = { DownloadsScreen() }
    ),
    MyTab(
        id = 2,
        title = R.string.tab_about,
        icon = R.drawable.settings,
        screen = { AboutScreen() }
    )
)

data class MyTab(
    val id: Int,
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val screen: @Composable () -> Unit
)
