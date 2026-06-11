package org.zl.team.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object ThemeManager {
    var isDarkMode by mutableStateOf(true)
    fun toggle() { isDarkMode = !isDarkMode }
}
