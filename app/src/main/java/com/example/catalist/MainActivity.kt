package com.example.catalist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.catalist.core.navigation.AppMainNavigation
import com.example.catalist.ui.theme.CatalistTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val darkTheme = isSystemInDarkTheme()
            val systemUiController = rememberSystemUiController()

            SideEffect {
                systemUiController.setSystemBarsColor(
                    color = Color.Transparent,
                    darkIcons = !darkTheme
                )
            }

            CatalistTheme(
                darkTheme = darkTheme,
                dynamicColor = false
            ) {
                AppMainNavigation()
            }
        }
    }
}
