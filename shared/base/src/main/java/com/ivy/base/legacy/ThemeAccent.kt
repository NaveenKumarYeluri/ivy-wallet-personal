package com.ivy.base.legacy

import androidx.compose.runtime.Immutable

@Immutable
enum class ThemeAccent(
    val title: String,
    val colorHex: Long,
    val lightHex: Long = colorHex,
    val darkHex: Long = colorHex
) {
    GREEN(
        title = "Ivy Emerald",
        colorHex = 0xFF27AE60,
        lightHex = 0xFF67B77E,
        darkHex = 0xFF1E824C
    ),
    PURPLE(
        title = "Royal Violet",
        colorHex = 0xFF8338EC,
        lightHex = 0xFF9B5DE5,
        darkHex = 0xFF5C24B8
    ),
    BLUE(
        title = "Ocean Blue",
        colorHex = 0xFF2979FF,
        lightHex = 0xFF69A1FF,
        darkHex = 0xFF1C54B2
    ),
    ORANGE(
        title = "Sunset Orange",
        colorHex = 0xFFFF6D00,
        lightHex = 0xFFFF9E40,
        darkHex = 0xFFB24C00
    ),
    PINK(
        title = "Rose Berry",
        colorHex = 0xFFE91E63,
        lightHex = 0xFFF48FB1,
        darkHex = 0xFFAD1457
    ),
    RED(
        title = "Ruby Red",
        colorHex = 0xFFE53935,
        lightHex = 0xFFEF5350,
        darkHex = 0xFFB71C1C
    );

    companion object {
        fun fromName(name: String?): ThemeAccent =
            entries.firstOrNull { it.name.equals(name, ignoreCase = true) } ?: GREEN
    }
}
