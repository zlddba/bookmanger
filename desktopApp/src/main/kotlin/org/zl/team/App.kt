package org.zl.team

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import org.zl.team.ui.ThemeManager
import org.zl.team.ui.login.LoginScreen
import org.zl.team.ui.main.MainScreen
import org.zl.team.util.SessionManager
import org.zl.team.service.BackupService

private val DarkColors = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D1B2A),
    primaryContainer = Color(0xFF1B3A5C),
    onPrimaryContainer = Color(0xFFD6E8FF),
    secondary = Color(0xFF80CBC4),
    onSecondary = Color(0xFF003733),
    secondaryContainer = Color(0xFF00504A),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF2C2F33),
    onSurfaceVariant = Color(0xFFC4C6D0),
    background = Color(0xFF111318),
    onBackground = Color(0xFFE3E2E6),
    error = Color(0xFFEF9A9A),
    onError = Color(0xFF601410),
    outline = Color(0xFF8E9099),
    outlineVariant = Color(0xFF44474E),
)

private val LightColors = lightColorScheme(
    primary = Color(0xFF1565C0),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD6E8FF),
    onPrimaryContainer = Color(0xFF001D36),
    secondary = Color(0xFF00796B),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFB2DFDB),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF0F0F0),
    onSurfaceVariant = Color(0xFF49454F),
    background = Color(0xFFF8F9FA),
    onBackground = Color(0xFF1C1B1F),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
)

@Composable
fun App() {
    val scheme = if (ThemeManager.isDarkMode) DarkColors else LightColors
    MaterialTheme(colorScheme = scheme) {
        // 启动时自动备份
        LaunchedEffect(Unit) {
            try { BackupService.autoBackup() } catch (_: Exception) { }
        }

        var loggedIn by remember { mutableStateOf(false) }

        if (!loggedIn) {
            LoginScreen(onLoginSuccess = { loggedIn = true })
        } else {
            MainScreen(onLogout = {
                SessionManager.logout()
                loggedIn = false
            })
        }
    }
}
