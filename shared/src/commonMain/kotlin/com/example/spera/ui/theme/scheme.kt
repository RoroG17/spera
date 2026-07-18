import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color

val LightColors = lightColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.White,
    primaryContainer = PurplePrimaryContainer,
    onPrimaryContainer = Color.Black,

    secondary = PinkSecondary,
    onSecondary = Color.White,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = Color.Black,

    background = BackgroundLight,
    onBackground = Color.Black,

    surface = SurfaceLight,
    onSurface = Color.Black,
)

val DarkColors = darkColorScheme(
    primary = PurplePrimary,
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF6D00B6),
    onPrimaryContainer = Color.White,

    secondary = PinkSecondary,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFFB0007A),
    onSecondaryContainer = Color.White,

    background = BackgroundDark,
    onBackground = Color.White,

    surface = SurfaceDark,
    onSurface = Color.White,
)