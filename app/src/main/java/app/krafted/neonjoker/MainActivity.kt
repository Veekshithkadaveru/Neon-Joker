package app.krafted.neonjoker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.krafted.neonjoker.ui.navigation.NeonJokerNavHost
import app.krafted.neonjoker.ui.theme.NeonJokerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonJokerTheme {
                NeonJokerNavHost()
            }
        }
    }
}
