package com.irmex.admin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.irmex.admin.presentation.navigation.AppRoot
import com.irmex.admin.presentation.theme.IrmexAdminTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            IrmexAdminTheme {
                Surface(Modifier.fillMaxSize()) { AppRoot() }
            }
        }
    }
}
