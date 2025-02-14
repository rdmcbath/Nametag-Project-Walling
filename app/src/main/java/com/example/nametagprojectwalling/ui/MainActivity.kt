package com.example.nametagprojectwalling.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.example.nametagprojectwalling.ui.theme.NametagProjectWallingTheme
import com.example.nametagprojectwalling.ui.screens.MainScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NametagProjectWallingTheme {
                    MainScreen(
                        viewModel = viewModel,
                    )
                }
            }
        }
    }
