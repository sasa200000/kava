package com.example.ui.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.data.model.AppLanguage
import com.example.ui.theme.PrimaryCyan
import com.example.ui.theme.ShieldDangerRed

data class BrowserError(
    val errorType: String,    // ERROR, HTTP, SECURITY
    val errorCode: Int,
    val description: String,
    val failingUrl: String
)

@Composable
fun BrowserErrorView(
    error: BrowserError?,
    language: AppLanguage,
    onReload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117))
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.CloudOff,
            contentDescription = "Error",
            tint = ShieldDangerRed,
            modifier = Modifier.size(72.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        val title = when (language) {
            AppLanguage.FA -> "بارگذاری صفحه ناموفق بود"
            AppLanguage.EN -> "Page Failed to Load"
        }
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        val desc = if (error != null) {
            "${error.errorType} (${error.errorCode}): ${error.description}\n${error.failingUrl}"
        } else {
            if (language == AppLanguage.FA) "خطای نامشخص رخ داد" else "Unknown error occurred"
        }
        Text(
            text = desc,
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF8B949E),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onReload,
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryCyan)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = if (language == AppLanguage.FA) "تلاش مجدد" else "Retry"
            )
        }
    }
}
