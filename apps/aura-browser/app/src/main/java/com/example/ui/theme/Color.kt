package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// 7 Rainbow Spectrum Colors
val RainbowRed = Color(0xFFFF2A6D)
val RainbowOrange = Color(0xFFFF7A00)
val RainbowYellow = Color(0xFFFFD600)
val RainbowGreen = Color(0xFF00F59B)
val RainbowCyan = Color(0xFF00F0FF)
val RainbowBlue = Color(0xFF3B82F6)
val RainbowViolet = Color(0xFFA855F7)

val RainbowPalette = listOf(
    RainbowRed,
    RainbowOrange,
    RainbowYellow,
    RainbowGreen,
    RainbowCyan,
    RainbowBlue,
    RainbowViolet,
    RainbowRed // loop back for continuous gradient
)

val RainbowBrush = Brush.horizontalGradient(RainbowPalette)

// Cyber Dark Colors
val CyberDarkBg = Color(0xFF0A0D14)
val CyberDarkSurface = Color(0xFF131822)
val CyberDarkSurfaceVariant = Color(0xFF1C2433)
val CyberBorder = Color(0xFF2B374E)
val CyberGlow = Color(0xFF00F0FF)

// M3 Color Scheme Colors
val PrimaryCyan = Color(0xFF00F0FF)
val OnPrimaryDark = Color(0xFF00363A)
val PrimaryContainerDark = Color(0xFF004F54)
val OnPrimaryContainerDark = Color(0xFF70F5FF)

val SecondaryViolet = Color(0xFFA855F7)
val OnSecondaryDark = Color(0xFF381E72)
val SecondaryContainerDark = Color(0xFF4F378B)

val TertiaryGreen = Color(0xFF00F59B)
val OnTertiaryDark = Color(0xFF003920)

val ShieldSecureGreen = Color(0xFF00E676)
val ShieldWarningOrange = Color(0xFFFF9100)
val ShieldDangerRed = Color(0xFFFF1744)
