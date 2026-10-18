package com.example.ui.browser

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AppLanguage
import com.example.data.model.BrowserTab
import com.example.ui.i18n.AppStrings
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDarkSurface
import com.example.ui.theme.CyberDarkSurfaceVariant
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.ShieldDangerRed
import com.example.ui.theme.ShieldSecureGreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecurityShieldSheet(
    tab: BrowserTab,
    totalBlocked: Int,
    adBlockerEnabled: Boolean,
    antiPhishingEnabled: Boolean,
    forceHttps: Boolean,
    language: AppLanguage,
    onToggleAdBlocker: (Boolean) -> Unit,
    onToggleAntiPhishing: (Boolean) -> Unit,
    onToggleForceHttps: (Boolean) -> Unit,
    onClearData: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = CyberDarkSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(ShieldSecureGreen.copy(alpha = 0.2f))
                            .border(1.5.dp, ShieldSecureGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Shield",
                            tint = ShieldSecureGreen,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = AppStrings.securityShield(language),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Connection Status Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDarkSurfaceVariant)
                    .border(1.dp, if (tab.isSecureHttps) ShieldSecureGreen.copy(alpha = 0.4f) else ShieldDangerRed, RoundedCornerShape(12.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (tab.isSecureHttps) Icons.Default.Lock else Icons.Default.Shield,
                    contentDescription = "Status",
                    tint = if (tab.isSecureHttps) ShieldSecureGreen else ShieldDangerRed,
                    modifier = Modifier.size(28.dp)
                )

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = if (tab.isSecureHttps) AppStrings.secureHttps(language) else AppStrings.connectionInsecure(language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (tab.isSecureHttps) ShieldSecureGreen else ShieldDangerRed
                    )
                    Text(
                        text = tab.certIssuer,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.LightGray
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trackers Stats Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CyberDarkSurfaceVariant)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${tab.blockedTrackersOnPage}",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = PrimaryCyan
                    )
                    Text(
                        text = if (language == AppLanguage.FA) "ردیاب مسدود در این برگه" else "Trackers on Page",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(CyberBorder)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "$totalBlocked",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.ExtraBold),
                        color = ShieldSecureGreen
                    )
                    Text(
                        text = if (language == AppLanguage.FA) "کل ردیاب‌های مسدود شده" else "Total Session Blocked",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = if (language == AppLanguage.FA) "سپر ضد هک فعال" else "Anti-Hack Defense Modules",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = PrimaryCyan
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Toggle 1: Ad & Tracker Blocker
            SecurityToggleItem(
                title = AppStrings.adBlocker(language),
                subtitle = if (language == AppLanguage.FA) "مسدودسازی تبلیغات مزاحم و اسکریپت‌های ردیابی" else "Blocks invasive ads and data scrapers",
                checked = adBlockerEnabled,
                onCheckedChange = onToggleAdBlocker
            )

            // Toggle 2: Anti-Phishing Guard
            SecurityToggleItem(
                title = AppStrings.antiPhishing(language),
                subtitle = if (language == AppLanguage.FA) "هشدار در برابر دامنه‌های جعلی و تله‌های فیشینگ" else "Real-time protection against malicious sites",
                checked = antiPhishingEnabled,
                onCheckedChange = onToggleAntiPhishing
            )

            // Toggle 3: Force HTTPS
            SecurityToggleItem(
                title = AppStrings.forceHttps(language),
                subtitle = if (language == AppLanguage.FA) "جلوگیری از ارسال اطلاعات به شکل رمزنگاری نشده" else "Auto-upgrades unencrypted connections to HTTPS",
                checked = forceHttps,
                onCheckedChange = onToggleForceHttps
            )

            // Toggle 4: Anti-Fingerprint (Always active indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppStrings.antiFingerprint(language),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                        color = Color.White
                    )
                    Text(
                        text = if (language == AppLanguage.FA) "محافظت در برابر استخراج اثرانگشت دستگاه و هویت" else "Prevents browser fingerprinting and canvas tracking",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Active",
                    tint = ShieldSecureGreen,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Wipe data button
            Button(
                onClick = {
                    onClearData()
                    Toast.makeText(context, if (language == AppLanguage.FA) "تمام کوکی‌ها و حافظه موقت پاک شدند" else "Cookies and cache cleared", Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("wipe_data_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ShieldDangerRed.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, ShieldDangerRed)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Clear",
                    tint = ShieldDangerRed,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppStrings.clearData(language),
                    color = ShieldDangerRed,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun SecurityToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = ShieldSecureGreen
            )
        )
    }
}
