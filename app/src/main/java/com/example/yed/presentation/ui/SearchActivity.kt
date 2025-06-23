package com.example.yed.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yed.R
import com.example.yed.ui.theme.YEDTheme


class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YEDTheme {
            }
        }
    }
}

@Preview
@Composable
fun SearchActivityPreview() {
    YEDTheme {
        SearchScreen()
    }
}

@Composable
fun SearchScreen(){
    var searchQuery by rememberSaveable { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                Logo()
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .widthIn(max = 488.dp)
                        .padding(start = 16.dp, end = 16.dp)
                        .fillMaxWidth(),
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(painter = painterResource(R.drawable.search), contentDescription = "Search") },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton (onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(24.dp)
                )
                WordDisplay("Meow")
            }
        }
    )
}

@Composable
fun WordDisplay(word: String) {
    Text(
        text = word.uppercase(),
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        modifier = Modifier.padding(16.dp),
    )

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        PronunciationDisplay(
            isActive = true,
            onClick = { /* Handle pronunciation click */ },
            pronunciation = "Meow",
            modifier = Modifier.weight(0.5f).padding(start = 5.dp)
        )

        PronunciationDisplay(
            isActive = false,
            onClick = { /* Handle pronunciation click */ },
            pronunciation = "КАT",
            modifier = Modifier.weight(0.5f).padding(start = 5.dp)
        )
    }

    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
    ) {
        AccentDisplay("UK", modifier = Modifier.weight(0.5f).padding(start = 5.dp))
        AccentDisplay("US", modifier = Modifier.weight(0.5f).padding(start = 5.dp))
    }
}

@Composable
fun PronunciationDisplay(
    isActive: Boolean,
    onClick: () -> Unit,
    pronunciation: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        IconButton(
                onClick = onClick,
            ) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "Pronunciation",
                    tint = if (isActive) Color(0xFF0C75A3) else Color.Gray
                )
            }

            Box (){
                BasicText(
                    text = pronunciation,
                    autoSize = TextAutoSize.StepBased(minFontSize = 5.sp, maxFontSize = 15.sp),
                    maxLines = 1,
                )
            }

    }
}

@Composable
fun AccentDisplay(
    accent: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ){
        Text(
            text = accent,
            style = TextStyle(
                fontSize = 12.sp,
                fontStyle = FontStyle.Italic,
                color = Color(0xFF0C75A3)
            ),
        )
    }
}

