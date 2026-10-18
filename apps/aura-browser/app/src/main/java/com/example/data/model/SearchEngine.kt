package com.example.data.model

enum class SearchEngine(
    val displayNameEn: String,
    val displayNameFa: String,
    val queryUrl: String,
    val homeUrl: String,
    val privacyFocused: Boolean
) {
    DUCKDUCKGO(
        displayNameEn = "DuckDuckGo",
        displayNameFa = "داک‌داک‌گو (ضد ردیابی)",
        queryUrl = "https://duckduckgo.com/?q=%s",
        homeUrl = "https://duckduckgo.com",
        privacyFocused = true
    ),
    BRAVE(
        displayNameEn = "Brave Search",
        displayNameFa = "بریو (حفظ کامل حریم خصوصی)",
        queryUrl = "https://search.brave.com/search?q=%s",
        homeUrl = "https://search.brave.com",
        privacyFocused = true
    ),
    GOOGLE(
        displayNameEn = "Google",
        displayNameFa = "گوگل (بزرگ‌ترین موتور جستجو)",
        queryUrl = "https://www.google.com/search?q=%s",
        homeUrl = "https://www.google.com",
        privacyFocused = false
    ),
    STARTPAGE(
        displayNameEn = "Startpage",
        displayNameFa = "استارت‌پیج (گوگل با حریم خصوصی)",
        queryUrl = "https://www.startpage.com/sp/search?query=%s",
        homeUrl = "https://www.startpage.com",
        privacyFocused = true
    ),
    BING(
        displayNameEn = "Bing",
        displayNameFa = "بینگ مایکروسافت",
        queryUrl = "https://www.bing.com/search?q=%s",
        homeUrl = "https://www.bing.com",
        privacyFocused = false
    ),
    ECOSIA(
        displayNameEn = "Ecosia",
        displayNameFa = "اکوزیا (کاشت درخت با جستجو)",
        queryUrl = "https://www.ecosia.org/search?q=%s",
        homeUrl = "https://www.ecosia.org",
        privacyFocused = true
    )
}
