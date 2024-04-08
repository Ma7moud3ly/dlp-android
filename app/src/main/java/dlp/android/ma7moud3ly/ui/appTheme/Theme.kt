package dlp.android.ma7moud3ly.ui.appTheme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val borderColor =Color(0xFFE8E8E8)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF1976D2), // Darker shade of blue
    secondary = Color(0xFFFFFFFF), // Darker shade of green
    tertiary = Color(0xFFFFA000), // Darker shade of yellow
    surface = Color(0xFF121212), // Dark gray
    background = Color(0xFF212121), // Slightly lighter dark gray
    onPrimary = Color(0xFF1976D2), // White
    onSecondary = Color(0xFFFFFFFF), // White
    onTertiary = Color(0xFFFFFFFF), // White
    onBackground = Color(0xFFFFFFFF), // White
    onSurface = Color(0xFFFFFFFF) // White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2196F3), // Blue
    secondary = Color(0xFF222222), // Black
    tertiary = Color(0xFFFFC107), // Yellow
    surface = Color(0xFFFFFFFF), // White
    background = Color(0xFFF5F5F5), // Light Gray
    onPrimary = Color(0xFFFFFFFF), // White
    onSecondary = Color(0xFFFFFFFF), // White
    onTertiary = Color(0xFF000000), // Black
    onBackground = Color(0xFF000000), // Black
    onSurface = Color(0xFF000000) // Black
)

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = appTypography(),
        content = content
    )
}