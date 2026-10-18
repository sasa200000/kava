package com.example.ui.browser

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AppLanguage
import com.example.data.model.SearchEngine
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.RainbowPalette
import com.example.ui.theme.SecondaryViolet
import com.example.ui.theme.ShieldSecureGreen

data class ShortcutItem(
    val name: String,
    val url: String,
    val icon: ImageVector,
    val color: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BrowserHomePage(
    searchEngine: SearchEngine,
    language: AppLanguage,
    totalBlocked: Int,
    onNavigate: (String) -> Unit,
    onOpenSecurity: () -> Unit,
    onOpenEdgeLighting: () -> Unit,
    onOpenDownloads: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    val shortcuts = listOf(
        ShortcutItem("DuckDuckGo", "https://duckduckgo.com", Icons.Default.Shield, PrimaryCyan),
        ShortcutItem("Google", "https://google.com", Icons.Default.Search, Color(0xFF4285F4)),
        ShortcutItem("Brave", "https://search.brave.com", Icons.Default.Lock, Color(0xFFFF5722)),
        ShortcutItem("Wikipedia", "https://wikipedia.org", Icons.Default.Language, Color(0xFF00E5FF)),
        ShortcutItem("GitHub", "https://github.com", Icons.Default.AutoAwesome, Color(0xFFA855F7)),
        ShortcutItem("SpeedTest", "https://fast.com", Icons.Default.Speed, ShieldSecureGreen)
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 7-Color Rainbow Browser Icon Header
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .border(
                    width = 3.dp,
                    brush = Brush.sweepGradient(RainbowPalette),
                    shape = CircleShape
                )
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_app_icon),
                contentDescription = "Aura Browser Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Title with rainbow shimmer style
        Text(
            text = if (language == AppLanguage.FA) "آئورا بروزر" else "Aura Browser",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = if (language == AppLanguage.FA)
                "مرورگر امن، ضد هک با رقص نور چند شکلی و موتورهای جستجوی حرفه‌ای"
            else
                "Next-Gen Anti-Hack Browser with Dynamic Rainbow Edge Lighting",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Security Status Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onOpenSecurity() }
                .testTag("security_status_card"),
            colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                        .border(1.5.dp, ShieldSecureGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Shield Active",
                        tint = ShieldSecureGreen,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (language == AppLanguage.FA) "سپر ضد هک فعال است" else "Anti-Hack Guard Active",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = ShieldSecureGreen
                        )
                    }
                    Text(
                        text = if (language == AppLanguage.FA)
                            "ردیاب‌های مسدود شده: $totalBlocked • رمزنگاری TLS 1.3"
                        else
                            "Blocked trackers: $totalBlocked • TLS 1.3 Strict",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Feature Quick Action Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenEdgeLighting() }
                    .testTag("home_edge_lighting_btn"),
                color = CyberDarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, SecondaryViolet.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Edge Lighting",
                        tint = SecondaryViolet,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FA) "رقص نور لبه" else "Edge Glow",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onOpenDownloads() }
                    .testTag("home_downloads_btn"),
                color = CyberDarkSurfaceVariant,
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryCyan.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Downloads",
                        tint = PrimaryCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (language == AppLanguage.FA) "دانلودها" else "Downloads",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.White
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Quick Shortcuts Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = AppStrings.quickShortcuts(language),
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            maxItemsInEachRow = 3
        ) {
            shortcuts.forEach { item ->
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onNavigate(item.url) }
                        .testTag("shortcut_${item.name}"),
                    colors = CardDefaults.cardColors(containerColor = CyberDarkSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyberBorder)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(item.color.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = item.icon,
                                contentDescription = item.name,
                                tint = item.color,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // Engine & Privacy info
        Text(
            text = if (language == AppLanguage.FA)
                "موتور پیش‌فرض: ${searchEngine.displayNameFa}"
            else
                "Current Engine: ${searchEngine.displayNameEn} (Encrypted)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
