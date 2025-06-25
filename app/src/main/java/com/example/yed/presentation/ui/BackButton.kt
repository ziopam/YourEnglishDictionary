package com.example.yed.presentation.ui

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.yed.R

@Composable
fun BackButton(
    modifier: Modifier
){
    val context = LocalContext.current

    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart,
    ) {
        IconButton(
            onClick = { (context as? Activity)?.finish() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.arrow_left),
                contentDescription = "Back Button",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}