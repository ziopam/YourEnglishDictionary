package com.example.yed.presentation.ui

import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yed.domain.entity.Word
import com.example.yed.presentation.ui.theme.YEDTheme
import com.example.yed.presentation.viewModels.LearnViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.abs
import kotlin.math.roundToInt

@AndroidEntryPoint
class LearnActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YEDTheme {
                LearnScreen()
            }
        }
    }
}

@Composable
fun LearnScreen(viewModel: LearnViewModel = hiltViewModel()) {
    val currentWord by viewModel.currentWord
    val isLoading by viewModel.isLoading
    val isFinished by viewModel.isFinished

    val mediaPlayer = remember { viewModel.mediaPlayer }

    LaunchedEffect(Unit) {
        viewModel.loadWordsToLearn()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ){
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .padding(top = 5.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    BackButton(
                        modifier = Modifier.fillMaxWidth()
                    )
                    Logo()
                }

                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        isLoading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        isFinished -> {
                            FinishedScreen(
                                onRestart = { viewModel.resetLearning() }
                            )
                        }

                        currentWord != null -> {
                            FlippableWordCard(
                                word = currentWord!!,
                                onSwipeLeft = { viewModel.onSwipeLeft() },
                                onSwipeRight = { viewModel.onSwipeRight() },
                                mediaPlayer = mediaPlayer
                            )
                        }
                    }
                }
            }

        }
    )
}

@Composable
fun FinishedScreen(onRestart: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🎉",
            fontSize = 64.sp,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Text(
            text = "Congrats!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "You've learned all words!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 32.dp)
        )
        
        Button(
            onClick = onRestart,
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Text("Start again")
        }
    }
}

@Composable
fun FlippableWordCard(
    word: Word,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    mediaPlayer: MediaPlayer
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableFloatStateOf(0f) }
    
    val rotationYAngle by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(
            durationMillis = 600,
            easing = FastOutSlowInEasing
        ),
        label = "Card Flip Animation"
    )


    LaunchedEffect(word) {
        isFlipped = false
        offsetX = 0f
    }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(400.dp)
            .offset { IntOffset(offsetX.roundToInt(), 0) }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        when {
                            offsetX < -150f -> {
                                onSwipeLeft()
                            }
                            offsetX > 150f -> {
                                onSwipeRight()
                            }
                        }
                        offsetX = 0f
                    },
                    onHorizontalDrag = { _, delta ->
                        offsetX += delta
                    }
                )
            }
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .shadow(12.dp, RoundedCornerShape(20.dp))
                .clickable { isFlipped = !isFlipped }
                .graphicsLayer {
                    rotationY = rotationYAngle
                    cameraDistance = 12f * density
                },
            colors = CardDefaults.cardColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(20.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (rotationYAngle <= 90f) {
                    WordFrontSide(word = word.word)
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                rotationY = 180f
                            }
                    ) {
                        WordBackSide(
                            word = word,
                            mediaPlayer = mediaPlayer
                        )
                    }
                }
            }
        }

        SwipeIndicators(offsetX = offsetX)
    }
}

@Composable
fun WordFrontSide(word: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Text(
            text = word.uppercase(),
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Press to see definition",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun WordBackSide(
    word: Word,
    mediaPlayer: MediaPlayer
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        WordDisplay(word = word, mediaPlayer = mediaPlayer)
    }
}

@Composable
fun SwipeIndicators(offsetX: Float) {
    val swipeThreshold = 150f
    val alpha = (abs(offsetX) / swipeThreshold).coerceIn(0f, 1f)
    
    if (offsetX < -50f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 32.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Green.copy(alpha = alpha * 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Learned ✓",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    } else if (offsetX > 50f) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(end = 32.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = Color.Red.copy(alpha = alpha * 0.8f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Don't know ✗",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

