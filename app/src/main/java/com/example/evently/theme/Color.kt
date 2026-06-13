package com.example.evently.theme

import androidx.compose.ui.graphics.Color

// Light Theme Colors
val PrimaryLight = Color(0xFF6C63FF)
val SecondaryLight = Color(0xFFFF6B6B)
val BackgroundLight = Color(0xFFF8F9FD)
val SurfaceLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF1A1A1A)
val TextSecondaryLight = Color(0xFF666666)
val SuccessColor = Color(0xFF22C55E)
val CardBorderLight = Color(0xFFEEEEFF)

// Dark Theme Colors
val PrimaryDark = Color(0xFF8B85FF)
val SecondaryDark = Color(0xFFFF8585)
val BackgroundDark = Color(0xFF0C0B12)
val SurfaceDark = Color(0xFF161520)
val TextPrimaryDark = Color(0xFFF5F5F7)
val TextSecondaryDark = Color(0xFFAAAAAA)
val CardBorderDark = Color(0xFF2C2B3C)

// Shared neutral/translucent tones
val TransparentGray = Color(0x111A1A1A)
val GrayBorder = Color(0xFFE5E7EB)
val LightYellow = Color(0xFFFFD166)
val OrangeWarn = Color(0xFFF77F00)

// Category gradient colors (start to end)
val GradientConcertStart = Color(0xFF7C3AED)
val GradientConcertEnd   = Color(0xFFDB2777)

val GradientComedyStart  = Color(0xFFEA580C)
val GradientComedyEnd    = Color(0xFFFBBF24)

val GradientWorkshopStart = Color(0xFF0891B2)
val GradientWorkshopEnd   = Color(0xFF6366F1)

val GradientSportsStart  = Color(0xFF16A34A)
val GradientSportsEnd    = Color(0xFF14B8A6)

val GradientNightlifeStart = Color(0xFF0F172A)
val GradientNightlifeEnd   = Color(0xFF7C3AED)

val GradientFestivalStart = Color(0xFFDC2626)
val GradientFestivalEnd   = Color(0xFFF97316)

val GradientDefaultStart = Color(0xFF6C63FF)
val GradientDefaultEnd   = Color(0xFF8B85FF)

// Per-category emoji map (used in HomeScreen pills)
val categoryEmojis: Map<String, String> = mapOf(
    "Concerts"   to "🎵",
    "Comedy"     to "😂",
    "Workshops"  to "🛠️",
    "Sports"     to "⚽",
    "Nightlife"  to "🌙",
    "Festivals"  to "🎉",
    "Tech"       to "💻",
    "Art"        to "🎨",
    "Food"       to "🍽️",
    "Theatre"    to "🎭"
)

// Returns a gradient pair for the given event category
fun categoryGradient(category: String): Pair<Color, Color> = when (category.lowercase()) {
    "concerts"   -> GradientConcertStart to GradientConcertEnd
    "comedy"     -> GradientComedyStart  to GradientComedyEnd
    "workshops"  -> GradientWorkshopStart to GradientWorkshopEnd
    "sports"     -> GradientSportsStart  to GradientSportsEnd
    "nightlife"  -> GradientNightlifeStart to GradientNightlifeEnd
    "festivals"  -> GradientFestivalStart to GradientFestivalEnd
    else         -> GradientDefaultStart to GradientDefaultEnd
}
