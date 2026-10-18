package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.SearchEngine
import com.example.security.SecurityEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun testAppName() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Aura Browser", appName)
    }

    @Test
    fun testTrackerBlockingEngine() {
        assertTrue(SecurityEngine.shouldBlockUrl("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"))
        assertTrue(SecurityEngine.shouldBlockUrl("https://www.google-analytics.com/analytics.js"))
        assertTrue(SecurityEngine.shouldBlockUrl("https://static.doubleclick.net/ad.js"))
    }

    @Test
    fun testSearchEngines() {
        val brave = SearchEngine.BRAVE
        assertTrue(brave.privacyFocused)
        val queryUrl = String.format(brave.queryUrl, "android")
        assertTrue(queryUrl.contains("search.brave.com"))
    }
}
