package com.example.data.model

enum class EdgeLightingPattern(val titleEn: String, val titleFa: String) {
    RAINBOW_CYCLE("7-Color Rainbow Flow", "چرخش هفت رنگ رنگین‌کمان"),
    EDGE_CHASE("Comet Neon Chase", "تعقیب نئونی ستاره‌ای"),
    CYBER_PULSE("Cyber Pulse & Breathing", "تپش نئونی سایبری"),
    SPECTRUM_WAVE("Multi-Shape Wave", "موج چند شکلی طیفی"),
    DUAL_GLOW("Dual Color Border", "درخشش دوگانه متضاد")
}

enum class EdgeLightingSpeed(val multiplier: Float, val labelEn: String, val labelFa: String) {
    SLOW(0.5f, "Slow", "آرام"),
    NORMAL(1.0f, "Normal", "متوسط"),
    FAST(1.8f, "Hyper Fast", "فوق‌العاده سریع")
}

enum class EdgeLightingTrigger(val labelEn: String, val labelFa: String) {
    ALWAYS("Always Active", "همیشه فعال"),
    WHILE_LOADING("While Page Loading", "فقط هنگام بارگذاری صفحه"),
    ON_DOWNLOAD("During Active Downloads", "هنگام دانلود فایل"),
    OFF("Disabled", "غیرفعال")
}

data class EdgeLightingConfig(
    val enabled: Boolean = true,
    val pattern: EdgeLightingPattern = EdgeLightingPattern.RAINBOW_CYCLE,
    val speed: EdgeLightingSpeed = EdgeLightingSpeed.NORMAL,
    val strokeWidthDp: Float = 6f, // 3f, 6f, 10f
    val cornerRadiusDp: Float = 28f,
    val trigger: EdgeLightingTrigger = EdgeLightingTrigger.ALWAYS,
    val glowIntensity: Float = 0.85f
)
