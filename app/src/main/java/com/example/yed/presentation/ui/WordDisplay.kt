package com.example.yed.presentation.ui

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yed.R
import com.example.yed.data.playAudioWithResultAsync
import com.example.yed.domain.entity.Meaning
import com.example.yed.domain.entity.Phonetic
import com.example.yed.domain.entity.Word
import com.example.yed.presentation.ui.theme.Purple40
import com.example.yed.presentation.ui.theme.SpecialBlue
import com.example.yed.presentation.ui.theme.SpecialGreen
import kotlinx.coroutines.launch

@Composable
fun WordDisplay(word: Word, mediaPlayer: MediaPlayer = MediaPlayer()) {

    LazyColumn (
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = word.word.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
            ) {
                for (i in word.phonetics.indices) {
                    val phonetic = word.phonetics[i]
                    PronunciationDisplay(
                        isActive = !phonetic.audio.isNullOrBlank(),
                        phonetic = phonetic,
                        mediaPlayer = mediaPlayer,
                        modifier = Modifier.weight(0.5f),
                        accent = if (i == 0) "UK" else "US"
                    )
                }
            }
        }

        item {
            SectionHeader("DEFINITIONS", color = Purple40)
        }

        word.meanings.forEach { meaning ->
            item {
                MeaningDisplay(meaning = meaning, modifier = Modifier.padding(horizontal = 16.dp))
            }
        }
    }

}

@Composable
fun WordDisplayWithoutScroll(
    word: Word,
    mediaPlayer: MediaPlayer = MediaPlayer()
) {
    Column(
        modifier = Modifier.padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = word.word.uppercase(),
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start,
        ) {
            for (i in word.phonetics.indices) {
                val phonetic = word.phonetics[i]
                PronunciationDisplay(
                    isActive = !phonetic.audio.isNullOrBlank(),
                    phonetic = phonetic,
                    mediaPlayer = mediaPlayer,
                    modifier = Modifier.weight(0.5f),
                    accent = if (i == 0) "UK" else "US"
                )
            }
        }

        SectionHeader("DEFINITIONS", color = Purple40)

        word.meanings.forEach { meaning ->
            MeaningDisplay(meaning = meaning, modifier = Modifier.padding(horizontal = 16.dp))
        }
    }
}

@Composable
fun PronunciationDisplay(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    mediaPlayer: MediaPlayer = MediaPlayer(),
    phonetic: Phonetic,
    accent: String
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            IconButton(onClick = {
                if (isActive) {
                    scope.launch {
                        mediaPlayer.playAudioWithResultAsync(phonetic.audio.toString()).onFailure {
                            Toast.makeText(context, "Error playing audio", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }) {
                Icon(
                    painter = painterResource(R.drawable.play),
                    contentDescription = "$accent pronunciation",
                    tint = if (isActive) SpecialBlue else Color.Gray
                )
            }

            BasicText(
                text = phonetic.text.toString(),
                autoSize = TextAutoSize.StepBased(minFontSize = 5.sp, maxFontSize = 15.sp),
                maxLines = 1,
            )
        }
        AccentDisplay(accent)
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
                fontSize = 18.sp,
                color = SpecialBlue
            ),
        )
    }
}

@Composable
fun SectionHeader(text: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = color
        )

        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = color,
            modifier = Modifier.padding(horizontal = 8.dp)
        )

        HorizontalDivider(
            modifier = Modifier.weight(1f),
            thickness = 1.dp,
            color = color
        )
    }
}

@Composable
fun MeaningDisplay(
    meaning: Meaning,
    modifier: Modifier = Modifier
) {
    Text(
        text=meaning.partOfSpeech,
        fontWeight = FontWeight.Bold,
        color = SpecialGreen,
        fontSize = 18.sp
    )

    Column(
        modifier = modifier.padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        meaning.definitions.forEach { definition ->
            Column (Modifier.fillMaxWidth().align(Alignment.Start)) {
                Text(
                    text = "•\t" + definition.definition,
                    fontSize = 15.sp,
                    color = Color.Black,
                )
                if (definition.example != null) {
                    Text(
                        text = "Example: ${definition.example}",
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(start = 5.dp),
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}