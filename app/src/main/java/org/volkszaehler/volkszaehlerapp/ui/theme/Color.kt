package org.volkszaehler.volkszaehlerapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Farbpalette für die Volkszähler App
 *
 * Basiert auf Material Design 3 Color System
 * https://m3.material.io/styles/color/the-color-system/key-colors-tones
 */

// ========== Primary Colors ==========

/**
 * Primary Color - Hauptfarbe der App
 * Verwendet für wichtige UI-Elemente wie FABs, prominente Buttons
 */
val Primary = Color(0xFF006B5A)
val PrimaryLight = Color(0xFF00A896)
val PrimaryDark = Color(0xFF004D40)

/**
 * On Primary - Textfarbe auf Primary
 */
val OnPrimary = Color(0xFFFFFFFF)

/**
 * Primary Container - Hintergrund für Primary-Elemente
 */
val PrimaryContainer = Color(0xFF9FF2E0)
val OnPrimaryContainer = Color(0xFF00201A)

// ========== Secondary Colors ==========

/**
 * Secondary Color - Sekundärfarbe
 * Verwendet für weniger prominente UI-Elemente
 */
val Secondary = Color(0xFF4A6360)
val SecondaryLight = Color(0xFF6D8A86)
val SecondaryDark = Color(0xFF2F4946)

/**
 * On Secondary - Textfarbe auf Secondary
 */
val OnSecondary = Color(0xFFFFFFFF)

/**
 * Secondary Container - Hintergrund für Secondary-Elemente
 */
val SecondaryContainer = Color(0xFFCCE8E3)
val OnSecondaryContainer = Color(0xFF051F1D)

// ========== Tertiary Colors ==========

/**
 * Tertiary Color - Tertiärfarbe
 * Verwendet für Akzente und Highlights
 */
val Tertiary = Color(0xFF416650)
val TertiaryLight = Color(0xFF5F8C76)
val TertiaryDark = Color(0xFF2A4A38)

/**
 * On Tertiary - Textfarbe auf Tertiary
 */
val OnTertiary = Color(0xFFFFFFFF)

/**
 * Tertiary Container - Hintergrund für Tertiary-Elemente
 */
val TertiaryContainer = Color(0xFFC3EDCF)
val OnTertiaryContainer = Color(0xFF002111)

// ========== Error Colors ==========

/**
 * Error Color - Fehlerfarbe
 * Verwendet für Fehlermeldungen und Warnungen
 */
val Error = Color(0xFFBA1A1A)
val ErrorLight = Color(0xFFFF5449)
val ErrorDark = Color(0xFF93000A)

/**
 * On Error - Textfarbe auf Error
 */
val OnError = Color(0xFFFFFFFF)

/**
 * Error Container - Hintergrund für Error-Elemente
 */
val ErrorContainer = Color(0xFFFFDAD6)
val OnErrorContainer = Color(0xFF410002)

// ========== Background Colors ==========

/**
 * Background - Haupthintergrundfarbe
 */
val Background = Color(0xFFFAFDFB)
val BackgroundDark = Color(0xFF191C1B)

/**
 * On Background - Textfarbe auf Background
 */
val OnBackground = Color(0xFF191C1B)
val OnBackgroundDark = Color(0xFFE1E3E0)

// ========== Surface Colors ==========

/**
 * Surface - Oberflächenfarbe für Cards, Sheets, etc.
 */
val Surface = Color(0xFFFAFDFB)
val SurfaceDark = Color(0xFF191C1B)

/**
 * On Surface - Textfarbe auf Surface
 */
val OnSurface = Color(0xFF191C1B)
val OnSurfaceDark = Color(0xFFE1E3E0)

/**
 * Surface Variant - Alternative Oberflächenfarbe
 */
val SurfaceVariant = Color(0xFFDAE5E1)
val SurfaceVariantDark = Color(0xFF3F4946)

/**
 * On Surface Variant - Textfarbe auf Surface Variant
 */
val OnSurfaceVariant = Color(0xFF3F4946)
val OnSurfaceVariantDark = Color(0xFFBFC9C5)

// ========== Outline Colors ==========

/**
 * Outline - Rahmenfarbe
 */
val Outline = Color(0xFF6F7976)
val OutlineDark = Color(0xFF899390)

/**
 * Outline Variant - Alternative Rahmenfarbe
 */
val OutlineVariant = Color(0xFFBFC9C5)
val OutlineVariantDark = Color(0xFF3F4946)

// ========== Custom Colors für Volkszähler ==========

/**
 * Chart Colors - Farben für Diagramme
 */
val ChartLine = Color(0xFF006B5A)
val ChartFill = Color(0x4D006B5A) // 30% Opacity
val ChartGrid = Color(0xFFE0E0E0)
val ChartAxis = Color(0xFF757575)

/**
 * Status Colors - Farben für Status-Anzeigen
 */
val StatusActive = Color(0xFF4CAF50)    // Grün
val StatusInactive = Color(0xFF9E9E9E)  // Grau
val StatusWarning = Color(0xFFFF9800)   // Orange
val StatusError = Color(0xFFF44336)     // Rot

/**
 * Energy Type Colors - Farben für verschiedene Energietypen
 */
val EnergyElectric = Color(0xFFFFB300)  // Gelb/Orange - Strom
val EnergyGas = Color(0xFF2196F3)       // Blau - Gas
val EnergyWater = Color(0xFF00BCD4)     // Cyan - Wasser
val EnergyHeat = Color(0xFFFF5722)      // Rot/Orange - Wärme
val EnergySolar = Color(0xFFFFC107)     // Gelb - Solar
val EnergyWind = Color(0xFF03A9F4)      // Hellblau - Wind

/**
 * Sensor Type Colors - Farben für verschiedene Sensortypen
 */
val SensorTemperature = Color(0xFFFF5722)  // Rot/Orange
val SensorHumidity = Color(0xFF2196F3)     // Blau
val SensorPressure = Color(0xFF9C27B0)     // Lila
val SensorPower = Color(0xFFFFB300)        // Gelb/Orange
val SensorVoltage = Color(0xFF4CAF50)      // Grün

/**
 * Data Quality Colors - Farben für Datenqualität
 */
val QualityGood = Color(0xFF4CAF50)      // Grün
val QualityMedium = Color(0xFFFF9800)    // Orange
val QualityPoor = Color(0xFFF44336)      // Rot
val QualityUnknown = Color(0xFF9E9E9E)   // Grau

/**
 * Gradient Colors - Farben für Verläufe
 */
val GradientStart = Color(0xFF006B5A)
val GradientMiddle = Color(0xFF00A896)
val GradientEnd = Color(0xFF9FF2E0)

/**
 * Scrim - Overlay-Farbe für Dialoge
 */
val Scrim = Color(0x80000000) // 50% Schwarz

/**
 * Inverse Colors - Invertierte Farben
 */
val InverseSurface = Color(0xFF2E3130)
val InverseOnSurface = Color(0xFFEFF1EE)
val InversePrimary = Color(0xFF80D5C4)

/**
 * Shadow Colors - Schattenfarben
 */
val Shadow = Color(0xFF000000)

/**
 * Surface Tint - Farbton für Oberflächen
 */
val SurfaceTint = Primary
val SurfaceTintDark = Color(0xFF80D5C4)