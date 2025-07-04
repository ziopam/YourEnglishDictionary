package com.example.yed.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.yed.R
import com.example.yed.presentation.ui.theme.Purple40
import com.example.yed.presentation.ui.theme.YEDTheme
import com.example.yed.presentation.viewModels.SearchViewModel
import com.example.yed.presentation.viewModels.WordSavingState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YEDTheme {
                SearchScreen()
            }
        }
    }
}

@Composable
fun SearchScreen(viewModel: SearchViewModel = hiltViewModel()){
    var searchQuery by rememberSaveable { mutableStateOf("") }
    val word by viewModel.wordState
    val isLoading by viewModel.loadingState
    val wordSavingState by viewModel.wordSavingState

    val keyboardController = LocalSoftwareKeyboardController.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            if(!isLoading && word != null) {
                WordButton(wordSavingState = wordSavingState, searchViewModel = viewModel)
            }
        },
        content = { innerPadding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize().padding(innerPadding)
            ) {
                BackButton(modifier = Modifier.fillMaxWidth().padding(top = 5.dp))
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
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            keyboardController?.hide()
                            viewModel.getWordDefinition(searchQuery)
                        }
                    )
                )

                Box(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    val wordValue = word
                    val errorMessage = viewModel.errorMessage
                    if (isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(100.dp).align(Alignment.Center))
                    } else if (wordValue != null) {
                        WordDisplay(word = wordValue, mediaPlayer = viewModel.mediaPlayer)
                    } else if (errorMessage != null) {
                        Text(
                            text = errorMessage,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        Text(
                            text = "Enter a word to search for its definition",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun WordButton(wordSavingState: WordSavingState, searchViewModel: SearchViewModel) {
    if (wordSavingState == WordSavingState.Saved) {
        FloatingActionButton(
            onClick = {searchViewModel.removeWordFromDictionary()},
        ) {
            Icon(
                painter = painterResource(R.drawable.check),
                contentDescription = "Already in dictionary",
                modifier = Modifier.size(30.dp)
            )
        }
    } else if (wordSavingState == WordSavingState.Loading) {
        FloatingActionButton(
            onClick = {},
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    } else {
        FloatingActionButton(
            onClick = {searchViewModel.addWordToDictionary()},
        ) {
            Icon(
                painter = painterResource(R.drawable.book),
                contentDescription = "Add to dictionary",
                modifier = Modifier.size(30.dp)
            )
        }
    }
}
