package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.screens.MainScreen
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.DailyLifeTrackerTheme
import com.example.ui.viewmodel.LifeTrackerViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: LifeTrackerViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      DailyLifeTrackerTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = BackgroundDark
        ) {
          MainScreen(viewModel = viewModel)
        }
      }
    }
  }
}

