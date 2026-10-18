package com.example.ui.browser

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.BrowserTab
import com.example.security.SecurityEngine
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.RainbowPalette
import com.example.ui.theme.ShieldDangerRed
import com.example.ui.theme.ShieldSecureGreen

@Composable
fun BrowserTopBar(
    tab: BrowserTab,
    totalTabs: Int,
    language: AppLanguage,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onOpenTabs: () -> Unit,
    onOpenSecurityShield: () -> Unit,
    onToggleBookmark: () -> Unit,
    onOpenDownloads: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleDesktop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }
    var isEditing by remember { mutableStateOf(false) }
    var textInput by remember(tab.url) {
        mutableStateOf(if (tab.url == "about:home") "" else tab.url)
    }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(CyberDarkSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Security Lock / Shield Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .clickable { onOpenSecurityShield() }
                    .testTag("security_shield_button"),
                contentAlignment = Alignment.Center
            ) {
                if (tab.url == "about:home") {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "Security Shield",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                } else if (tab.isSecureHttps) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "HTTPS Secure",
                        tint = ShieldSecureGreen,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Insecure HTTP",
                        tint = ShieldDangerRed,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Omnibox (Search / Address Bar)
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, if (isEditing) PrimaryCyan else CyberBorder, RoundedCornerShape(22.dp))
                    .clickable {
                        isEditing = true
                    }
                    .testTag("address_bar_container"),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isEditing) {
                        Text(
                            text = if (tab.url == "about:home") AppStrings.searchOrEnterUrl(language) else SecurityEngine.formatDisplayUrl(tab.url),
                            style = TextStyle(
                                color = if (tab.url == "about:home") MaterialTheme.colorScheme.onSurfaceVariant else Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (tab.url == "about:home") FontWeight.Normal else FontWeight.Medium
                            ),
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        if (tab.url != "about:home") {
                            IconButton(
                                onClick = onReload,
                                modifier = Modifier.size(28.dp).testTag("reload_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    } else {
                        BasicTextField(
                            value = textInput,
                            onValueChange = { textInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .focusRequester(focusRequester)
                                .testTag("address_input_field"),
                            singleLine = true,
                            textStyle = TextStyle(
                                color = Color.White,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(PrimaryCyan),
                            keyboardOptions = KeyboardOptions(
                                imeAction = ImeAction.Go,
                                keyboardType = KeyboardType.Uri
                            ),
                            keyboardActions = KeyboardActions(
                                onGo = {
                                    if (textInput.isNotBlank()) {
                                        onNavigate(textInput)
                                        isEditing = false
                                        focusManager.clearFocus()
                                    }
                                }
                            )
                        )

                        LaunchedEffect(isEditing) {
                            if (isEditing) {
                                focusRequester.requestFocus()
                            }
                        }

                        if (textInput.isNotEmpty()) {
                            IconButton(
                                onClick = { textInput = "" },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                if (textInput.isNotBlank()) {
                                    onNavigate(textInput)
                                    isEditing = false
                                    focusManager.clearFocus()
                                }
                            },
                            modifier = Modifier.size(28.dp).testTag("go_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Go",
                                tint = PrimaryCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Tabs button with badge count
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, CyberBorder, RoundedCornerShape(8.dp))
                    .clickable { onOpenTabs() }
                    .testTag("tabs_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = totalTabs.toString(),
                    style = TextStyle(
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // 3-dots Menu
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(38.dp).testTag("menu_more_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false },
                    modifier = Modifier.background(CyberDarkSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text(AppStrings.addToBookmarks(language), color = Color.White) },
                        onClick = {
                            menuExpanded = false
                            onToggleBookmark()
                        }
                    )
                    DropdownMenuItem(
                        text = {
                            Text(
                                if (tab.isDesktopMode)
                                    (if (language == AppLanguage.FA) "نسخه موبایل" else "Mobile Site")
                                else
                                    AppStrings.desktopMode(language),
                                color = Color.White
                            )
                        },
                        onClick = {
                            menuExpanded = false
                            onToggleDesktop()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(AppStrings.downloads(language), color = Color.White) },
                        onClick = {
                            menuExpanded = false
                            onOpenDownloads()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text(AppStrings.settings(language), color = Color.White) },
                        onClick = {
                            menuExpanded = false
                            onOpenSettings()
                        }
                    )
                }
            }
        }

        // Animated Rainbow Loading Bar
        AnimatedVisibility(visible = tab.isLoading && tab.progress < 100) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(Brush.horizontalGradient(RainbowPalette))
            )
        }
    }
}
