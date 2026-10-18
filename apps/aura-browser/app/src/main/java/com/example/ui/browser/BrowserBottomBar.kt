package com.example.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.RainbowPalette
import com.example.ui.theme.SecondaryViolet

@Composable
fun BrowserBottomBar(
    canGoBack: Boolean,
    canGoForward: Boolean,
    edgeLightingEnabled: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenEdgeLighting: () -> Unit,
    onOpenSecurity: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberDarkSurface)
            .border(
                width = 1.dp,
                color = CyberBorder,
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .navigationBarsPadding(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Back
        IconButton(
            onClick = onBack,
            modifier = Modifier.testTag("nav_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }

        // Forward
        IconButton(
            onClick = onForward,
            modifier = Modifier.testTag("nav_forward_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Forward",
                tint = Color.White
            )
        }

        // Home
        IconButton(
            onClick = onHome,
            modifier = Modifier.testTag("nav_home_button")
        ) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = PrimaryCyan
            )
        }

        // Bookmarks & History
        IconButton(
            onClick = onOpenBookmarksHistory,
            modifier = Modifier.testTag("nav_bookmarks_button")
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Bookmarks",
                tint = Color.White
            )
        }

        // Edge Lighting Magic Button (with 7-color glowing ring)
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(if (edgeLightingEnabled) SecondaryViolet.copy(alpha = 0.2f) else Color.Transparent)
                .border(
                    width = 2.dp,
                    brush = Brush.sweepGradient(RainbowPalette),
                    shape = CircleShape
                )
                .clickable { onOpenEdgeLighting() }
                .testTag("nav_edge_lighting_button"),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = "Edge Lighting",
                tint = if (edgeLightingEnabled) Color(0xFF00F0FF) else Color.Gray,
                modifier = Modifier.size(22.dp)
            )
        }

        // Security Shield Button
        IconButton(
            onClick = onOpenSecurity,
            modifier = Modifier.testTag("nav_security_button")
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = "Security",
                tint = Color(0xFF00E676)
            )
        }
    }
}
