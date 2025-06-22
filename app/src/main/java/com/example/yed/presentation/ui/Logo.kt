package com.example.yed.presentation.ui

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import com.example.yed.R

@Composable
fun Logo(modifier: Modifier = Modifier){
    Icon(
        painter = painterResource(R.drawable.logo),
        contentDescription = "App Logo",
        tint = Color.Unspecified,
        modifier = modifier
    )
}