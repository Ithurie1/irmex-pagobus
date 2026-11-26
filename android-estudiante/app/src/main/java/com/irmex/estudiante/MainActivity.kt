package com.irmex.estudiante

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.irmex.estudiante.presentation.navigation.AppRoot
import com.irmex.estudiante.presentation.theme.IrmexEstudianteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IrmexEstudianteTheme { AppRoot() }
        }
    }
}
