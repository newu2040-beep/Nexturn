package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.ui.NexturnDashboard
import com.example.ui.DocumentViewModel
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val viewModel = ViewModelProvider(
            this, 
            ViewModelProvider.AndroidViewModelFactory(application)
        )[DocumentViewModel::class.java]
        
        setContent {
            val isDark by viewModel.isDarkTheme.collectAsState()
            val selectedThemeIndex by viewModel.selectedThemeIndex.collectAsState()
            
            MyApplicationTheme(
                selectedThemeIndex = selectedThemeIndex,
                darkTheme = isDark,
                dynamicColor = false
            ) { 
                NexturnDashboard(viewModel)
            }
        }
    }
}
