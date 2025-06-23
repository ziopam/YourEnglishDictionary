package com.example.yed.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.yed.R
import com.example.yed.domain.entity.Definition
import com.example.yed.domain.entity.Meaning
import com.example.yed.domain.entity.Phonetic
import com.example.yed.domain.entity.Word
import com.example.yed.presentation.ui.theme.Purple40
import com.example.yed.presentation.ui.theme.YEDTheme


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
                        .padding(start = 16.dp, end = 16.dp, bottom = 5.dp)
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
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Purple40,
                        unfocusedIndicatorColor = Purple40,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                    )
                )

                WordDisplay(
                    word = Word(word = "ubiquitous",
                        phonetics = listOf(
                            Phonetic(
                                text = "/juːˈbɪk.wə.təs/",
                                audio = "https://api.dictionaryapi.dev/media/pronunciations/en/ubiquitous-uk.mp3"
                            ),
                            Phonetic(
                                text = "/juˈbɪk.wɪ.təs/",
                                audio = "https://api.dictionaryapi.dev/media/pronunciations/en/ubiquitous-us.mp3"
                            )
                        ),
                        meanings = listOf(
                            Meaning(
                                partOfSpeech = "adjective",
                                definitions = listOf(
                                    Definition(
                                        definition = "Being everywhere at once: omnipresent.",
                                        example = "To Hindus, Jews, Christians, and Muslims, God is ubiquitous."
                                    ),
                                    Definition(
                                        definition = "Широко распространённый, повсеместный",
                                        example = "Plastic загрязнение стало повсеместной экологической проблемой."
                                    )
                                )
                            ),
                            Meaning(
                                partOfSpeech = "noun",
                                definitions = listOf(
                                    Definition(
                                        definition = "Что-то, что присутствует повсюду",
                                        example = null
                                    )
                                )
                            )
                        ),)
                )
            }
        }
    )
}

