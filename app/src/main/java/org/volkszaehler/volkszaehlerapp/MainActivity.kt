// MainActivity.kt
// Datei: app/src/main/java/org/volkszaehler/volkszaehlerapp/MainActivity.kt

package org.volkszaehler.volkszaehlerapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import org.volkszaehler.volkszaehlerapp.ui.navigation.VolkszaehlerNavHost
import org.volkszaehler.volkszaehlerapp.ui.theme.VolkszaehlerTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            VolkszaehlerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    VolkszaehlerNavHost(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}