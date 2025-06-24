package com.example.yed.presentation.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yed.R
import com.example.yed.presentation.ui.theme.YEDTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YEDTheme {
                MainScreen({
                    val intent = Intent(this, SearchActivity::class.java)
                    startActivity(intent)
                }, {}, {}, {})
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MainScreen(
    onSearchClick: () -> Unit,
    onWordsClick: () -> Unit,
    onLearnClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                val buttonsHeight = maxHeight * 0.5f

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Logo()

                    MainButtonsGrid(
                        onSearchClick = onSearchClick,
                        onWordsClick = onWordsClick,
                        onLearnClick = onLearnClick,
                        onSettingsClick = onSettingsClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(buttonsHeight)
                            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
                    )
                }
            }
        }
    )
}



@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MainButtonsGrid(
    onSearchClick: () -> Unit,
    onWordsClick: () -> Unit,
    onLearnClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        val tableSize = 2
        val space = 10.dp
        val cellSize = with(LocalDensity.current) {
            val cellWidth = (maxWidth - space * (tableSize - 1)) / tableSize
            val cellHeight = (maxHeight - space * (tableSize - 1)) / tableSize
            minOf(cellWidth, cellHeight)
        }

        val buttons = listOf(
            ButtonData("Search", R.drawable.search, onSearchClick, 0),
            ButtonData("My words", R.drawable.book, onWordsClick, 100),
            ButtonData("Learn", R.drawable.pen_tool, onLearnClick, 200),
            ButtonData("Settings", R.drawable.settings, onSettingsClick, 300)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(tableSize),
            verticalArrangement = Arrangement.spacedBy(space, Alignment.CenterVertically),
            horizontalArrangement = Arrangement.spacedBy(space, Alignment.CenterHorizontally),
            modifier = Modifier.fillMaxSize()
        ) {
            buttons.forEach { button ->
                item(key = button.text) {
                    Box(modifier = Modifier.size(cellSize).padding(5.dp)) {
                        MainMenuButton(
                            text = button.text,
                            iconId = button.iconId,
                            action = button.action,
                            delay = button.delay
                        )
                    }
                }
            }
        }
    }
}



@Composable
fun MainMenuButton(
    text: String,
    iconId: Int,
    action: () -> Unit,
    delay: Int = 0
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(delay.toLong())
        visible = true
    }

    AnimatedVisibility (
        visible = visible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { 50 },
            animationSpec = tween(500)
        )
    ) {
        Card(
            onClick = action,
            shape = RoundedCornerShape(15.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .fillMaxSize()
                        .padding(5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = "Icon for $text",
                        modifier = Modifier
                            .padding(top = 30.dp)
                            .weight(0.65f)
                            .aspectRatio(1f)

                    )


                    Box(
                        modifier = Modifier
                            .weight(0.35f)
                            .fillMaxWidth()
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        BasicText(
                            text = text,
                            autoSize = TextAutoSize.StepBased(maxFontSize = 20.sp),
                            maxLines = 1
                        )
                    }

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YEDTheme {
        MainScreen({}, {}, {}, {})
    }
}

private data class ButtonData(
    val text: String,
    val iconId: Int,
    val action: () -> Unit,
    val delay: Int = 0
)