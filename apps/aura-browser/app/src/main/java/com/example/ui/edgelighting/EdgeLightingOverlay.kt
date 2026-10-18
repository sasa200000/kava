package com.example.ui.edgelighting

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.data.model.EdgeLightingConfig
import com.example.data.model.EdgeLightingPattern
import com.example.data.model.EdgeLightingSpeed
import com.example.ui.theme.RainbowPalette
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EdgeLightingOverlay(
    config: EdgeLightingConfig,
    isPageLoading: Boolean,
    isDownloading: Boolean,
    modifier: Modifier = Modifier
) {
    if (!config.enabled) return

    val shouldShow = when (config.trigger) {
        com.example.data.model.EdgeLightingTrigger.ALWAYS -> true
        com.example.data.model.EdgeLightingTrigger.WHILE_LOADING -> isPageLoading
        com.example.data.model.EdgeLightingTrigger.ON_DOWNLOAD -> isDownloading
        com.example.data.model.EdgeLightingTrigger.OFF -> false
    }

    if (!shouldShow) return

    val durationMs = when (config.speed) {
        EdgeLightingSpeed.FAST -> 1500
        EdgeLightingSpeed.NORMAL -> 3000
        EdgeLightingSpeed.SLOW -> 6000
    }

    val infiniteTransition = rememberInfiniteTransition(label = "EdgeLighting")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phase"
    )

    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = (durationMs * 0.7f).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val strokeWidthPx = config.strokeWidthDp.dp.toPx()
        val cornerRadiusPx = config.cornerRadiusDp.dp.toPx()

        // Create rounded rect path inset by half stroke width so it doesn't clip
        val inset = strokeWidthPx / 2f
        val path = Path().apply {
            addRoundRect(
                RoundRect(
                    left = inset,
                    top = inset,
                    right = width - inset,
                    bottom = height - inset,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            )
        }

        val brush = when (config.pattern) {
            EdgeLightingPattern.RAINBOW_CYCLE -> {
                // Continuous 7-color revolving sweep gradient
                Brush.sweepGradient(
                    colors = RainbowPalette,
                    center = Offset(width / 2f, height / 2f)
                )
            }
            EdgeLightingPattern.EDGE_CHASE -> {
                // Orbiting comet light spots
                val rad = (phase * PI / 180f).toFloat()
                val cx = width / 2f + (width / 2.2f) * cos(rad)
                val cy = height / 2f + (height / 2.2f) * sin(rad)
                Brush.radialGradient(
                    colors = listOf(
                        Color(0xFF00F0FF),
                        Color(0xFFA855F7),
                        Color(0xFFFF2A6D),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = width.coerceAtLeast(height) * 0.75f
                )
            }
            EdgeLightingPattern.CYBER_PULSE -> {
                // Breathing multi-color cyber neon
                val colors = listOf(
                    Color(0xFF00F0FF).copy(alpha = pulseGlow),
                    Color(0xFF00F59B).copy(alpha = pulseGlow * 0.9f),
                    Color(0xFFA855F7).copy(alpha = pulseGlow),
                    Color(0xFFFF2A6D).copy(alpha = pulseGlow * 0.85f),
                    Color(0xFF00F0FF).copy(alpha = pulseGlow)
                )
                Brush.linearGradient(
                    colors = colors,
                    start = Offset(0f, 0f),
                    end = Offset(width, height)
                )
            }
            EdgeLightingPattern.SPECTRUM_WAVE -> {
                // Sinusoidal wave shifting across screen
                val offsetFraction = (phase % 360f) / 360f
                val startX = width * offsetFraction
                val endX = startX + width
                Brush.linearGradient(
                    colors = RainbowPalette,
                    start = Offset(startX, 0f),
                    end = Offset(endX, height)
                )
            }
            EdgeLightingPattern.DUAL_GLOW -> {
                // Cyan and Magenta dual confrontation
                val rad = (phase * PI / 180f).toFloat()
                val start = Offset(width / 2f + (width / 2f) * cos(rad), height / 2f + (height / 2f) * sin(rad))
                val end = Offset(width / 2f - (width / 2f) * cos(rad), height / 2f - (height / 2f) * sin(rad))
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF00F0FF),
                        Color(0xFF131822),
                        Color(0xFFFF2A6D),
                        Color(0xFF131822),
                        Color(0xFF00F0FF)
                    ),
                    start = start,
                    end = end
                )
            }
        }

        // Layer 1: Soft ambient outer blur glow (simulated with wider transparent stroke)
        drawPath(
            path = path,
            brush = brush,
            alpha = 0.35f * config.glowIntensity,
            style = Stroke(width = strokeWidthPx * 2.5f)
        )

        // Layer 2: Main vivid neon edge
        drawPath(
            path = path,
            brush = brush,
            alpha = 0.95f * config.glowIntensity,
            style = Stroke(width = strokeWidthPx)
        )

        // Layer 3: High-energy core line
        drawPath(
            path = path,
            brush = Brush.radialGradient(
                colors = listOf(Color.White.copy(alpha = 0.6f * config.glowIntensity), Color.Transparent),
                center = Offset(width / 2f, 0f),
                radius = width
            ),
            style = Stroke(width = strokeWidthPx * 0.4f)
        )
    }
}
