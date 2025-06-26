package com.example.yed.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yed.R
import com.example.yed.domain.entity.Word
import com.example.yed.presentation.ui.theme.Purple40
import com.example.yed.presentation.ui.theme.SpecialBlue
import com.example.yed.presentation.ui.theme.YEDTheme
import com.example.yed.presentation.viewModels.MyDictionaryViewModel
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.foundation.lazy.items

@AndroidEntryPoint
class MyDictionaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YEDTheme {
                MyDictionaryScreen()
            }
        }
    }
}

@Composable
fun MyDictionaryScreen(viewModel: MyDictionaryViewModel = hiltViewModel()) {
    val currentWords = viewModel.currentWords
    val isLoading by viewModel.loadingState

    LaunchedEffect(key1 = true) {
        viewModel.loadWords()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        content = { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                BackButton(modifier = Modifier.fillMaxWidth().padding(top = 5.dp))
                Logo()
                if (!isLoading) {
                    if (currentWords.isEmpty()){
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No words found in your dictionary. Use search to add them.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = Color.DarkGray
                            )
                        }
                    } else {
                        WordCardList(words = currentWords, viewModel = viewModel)
                    }
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp))
                    }
                }
            }
        }
    )
}

@Composable
fun WordCardList(words: List<Word>, viewModel: MyDictionaryViewModel) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(
            items = words,
            key = { it.word }
        ) { word ->
            ExpandableWordCard(modifier = Modifier.animateItem(), word = word, viewModel = viewModel)
        }
    }
}

@Composable
fun ExpandableWordCard(modifier: Modifier = Modifier, word: Word, viewModel: MyDictionaryViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var isToLearn by remember { mutableStateOf(word.isToLearn) }

    Box(
        modifier = modifier
            .shadow(10.dp, RoundedCornerShape(16.dp), clip = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .animateContentSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = { expanded = !expanded }
                ),
            shape = RoundedCornerShape(16.dp,),
            border = BorderStroke(2.dp, Purple40),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = word.word,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if(word.isToLearn){
                                viewModel.markWordAsLearned(word)
                                word.isToLearn = false
                                isToLearn = false
                            }else {
                                viewModel.markWordToLearn(word)
                                word.isToLearn = true
                                isToLearn = true
                            }
                        }
                    ){
                        Icon(
                            painter = painterResource(id = R.drawable.pen_tool),
                            contentDescription = "Is to learn",
                            tint = if (isToLearn) SpecialBlue else Color.DarkGray,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    }

                    IconButton(onClick = {viewModel.deleteWord(word)}) {
                        Icon(
                            painter = painterResource(R.drawable.trash),
                            contentDescription = "Delete Word",
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }

                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = "Expand"
                        )
                    }
                }

                if (expanded) {
                    Spacer(modifier = Modifier.height(12.dp))
                    WordDisplayWithoutScroll(word = word)
                }
            }
        }
    }
}