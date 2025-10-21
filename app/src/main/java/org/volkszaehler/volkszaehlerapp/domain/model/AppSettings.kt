package org.volkszaehler.volkszaehlerapp.domain.model

/**
 * Settings data model
 */
data class AppSettings(
    val serverUrl: String = "http://demo.volkszaehler.org/middleware.php",
    val username: String = "",
    val password: String = "",
    val privateChannelUuids: String = "",
    val zeroBasedYAxis: Boolean = false,
    val autoReload: Boolean = false,
    val sortChannelMode: SortMode = SortMode.GROUPS,
    val tuples: Int = 1000
)

/**
 * Channel sort mode
 */
enum class SortMode(val displayName: String) {
    GROUPS("Groups"),
    TITLE("Title"),
    TYPE("Type")
}
