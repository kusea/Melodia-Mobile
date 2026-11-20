package com.melodia.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.melodia.app.presentation.MelodiaAppRoot
import com.melodia.app.presentation.theme.DarkBackground
import com.melodia.app.presentation.theme.MelodiaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as MelodiaApp).container

        setContent {
            MelodiaTheme(darkTheme = true) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DarkBackground
                ) {
                    MelodiaAppRoot(container = appContainer)
                }
            }
        }
    }
}
