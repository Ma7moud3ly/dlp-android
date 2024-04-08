package dlp.android.ma7moud3ly.ui.appTheme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dlp.android.ma7moud3ly.R


/**
 * Body - Regular - 12,14,16
 */
private val bodySmall = TextStyle(
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal
)

private val bodyMedium = bodySmall.copy(
    fontSize = 14.sp,
)

private val bodyLarge = bodySmall.copy(
    fontSize = 16.sp,
)

/**
 * Label - Medium - 11,12,14
 */

private val labelSmall = TextStyle(
    fontSize = 11.sp,
    fontWeight = FontWeight.Medium
)
private val labelMedium = labelSmall.copy(
    fontSize = 12.sp,
)
private val labelLarge = labelSmall.copy(
    fontSize = 14.sp,
)

/**
 * Label - Medium - 16,18,22
 */

private val titleSmall = TextStyle(
    fontSize = 16.sp,
    fontWeight = FontWeight.Medium
)
private val titleMedium = titleSmall.copy(
    fontSize = 18.sp,
)
private val titleLarge = titleSmall.copy(
    fontSize = 22.sp,
)


private val appFont = FontFamily(
    Font(R.font.ubuntu_light, FontWeight.Light),
    Font(R.font.ubuntu_regular, FontWeight.Normal),
    Font(R.font.ubuntu_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.ubuntu_medium, FontWeight.Medium),
    Font(R.font.ubuntu_bold, FontWeight.Bold)
)

// Set of Material typography styles to start with
fun appTypography() = Typography(
    bodySmall = bodySmall.copy(fontFamily = appFont),
    bodyMedium = bodyMedium.copy(fontFamily = appFont),
    bodyLarge = bodyLarge.copy(fontFamily = appFont),
    labelSmall = labelSmall.copy(fontFamily = appFont),
    labelMedium = labelMedium.copy(fontFamily = appFont),
    labelLarge = labelLarge.copy(fontFamily = appFont),
    titleSmall = titleSmall.copy(fontFamily = appFont),
    titleMedium = titleMedium.copy(fontFamily = appFont),
    titleLarge = titleLarge.copy(fontFamily = appFont)
)
