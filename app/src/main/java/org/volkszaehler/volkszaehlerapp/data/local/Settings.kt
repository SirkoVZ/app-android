package org.volkszaehler.volkszaehlerapp.data.local

data class Settings(
    val serverUrl: String = "https://demo.volkszaehler.org/middleware.php",
    val username: String = "",
    val password: String = "",
    val tuples: Int = 1000,
    val zeroBasedYAxis: Boolean = false,
    val autoReload: Boolean = false,
    val privateChannelUUIDs: String = "",
    val sortChannelMode: String = "off" // off, groups, plain
)
