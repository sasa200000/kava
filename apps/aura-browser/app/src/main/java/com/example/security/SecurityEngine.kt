package com.example.security

import android.net.Uri
import android.webkit.WebResourceResponse
import java.io.ByteArrayInputStream

object SecurityEngine {

    // Known tracker, analytics, cryptominer, and aggressive ad networks
    private val TRACKER_DOMAINS = hashSetOf(
        "doubleclick.net",
        "google-analytics.com",
        "googlesyndication.com",
        "adservice.google.com",
        "scorecardresearch.com",
        "adnxs.com",
        "criteo.com",
        "criteo.net",
        "taboola.com",
        "outbrain.com",
        "hotjar.com",
        "adroll.com",
        "coinhive.com",
        "popads.net",
        "exoclick.com",
        "propellerads.com",
        "adcash.com",
        "juicyads.com",
        "trafficjunky.net",
        "clickadu.com",
        "ero-advertising.com",
        "adsterra.com",
        "yllix.com",
        "zergnet.com",
        "revcontent.com",
        "mgid.com",
        "quantserve.com",
        "chartbeat.com",
        "matomo.org",
        "mixpanel.com",
        "segment.io",
        "optimizely.com",
        "braze.com",
        "appsflyer.com"
    )

    private val SUSPICIOUS_KEYWORDS = listOf(
        "free-gift-card",
        "crypto-giveaway",
        "login-security-verify",
        "account-update-urgent",
        "claim-prize-now"
    )

    /**
     * Checks if a request URL belongs to an ad network, tracker, or spyware
     */
    fun shouldBlockUrl(url: String?): Boolean {
        if (url.isNullOrBlank()) return false
        val lower = url.lowercase()

        try {
            val uri = Uri.parse(lower)
            val host = uri.host ?: return false

            for (tracker in TRACKER_DOMAINS) {
                if (host == tracker || host.endsWith(".$tracker")) {
                    return true
                }
            }

            // Also check path keywords for tracking beacons
            if (host.contains("tracker") || host.contains("telemetry") || host.contains("analytics")) {
                if (!host.contains("google.com") && !host.contains("github.com")) {
                    return true
                }
            }
        } catch (_: Exception) {
            return false
        }

        return false
    }

    /**
     * Creates an empty dummy response to cleanly block network traffic
     */
    fun createEmptyBlockedResponse(): WebResourceResponse {
        return WebResourceResponse(
            "text/plain",
            "UTF-8",
            ByteArrayInputStream(ByteArray(0))
        )
    }

    /**
     * Inspects whether the URL might be a phishing or scam attempt
     */
    fun isPotentiallyUnsafe(url: String?): Boolean {
        if (url == null) return false
        val lower = url.lowercase()
        for (kw in SUSPICIOUS_KEYWORDS) {
            if (lower.contains(kw)) return true
        }
        return false
    }

    /**
     * Formats URL for clean display in address bar
     */
    fun formatDisplayUrl(url: String): String {
        return try {
            val uri = Uri.parse(url)
            val host = uri.host ?: url
            val scheme = uri.scheme ?: "https"
            if (scheme == "https") host else "$scheme://$host"
        } catch (_: Exception) {
            url
        }
    }
}
