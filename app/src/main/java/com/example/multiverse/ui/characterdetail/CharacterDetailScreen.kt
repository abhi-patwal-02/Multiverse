package com.example.multiverse.ui.characterdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun CharacterDetailScreen(
    viewModel: CharacterDetailViewModel,
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    when {

        uiState.isLoading -> {

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Text(
                    text = uiState.error
                        ?: "Something went wrong"
                )

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Button(
                    onClick = viewModel::loadCharacter
                ) {
                    Text("Retry")
                }
            }
        }

        uiState.character != null -> {

            val character = uiState.character!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 16.dp)
            ){
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    item {

                        Button(
                            onClick = onBackClick
                        ) {
                            Text("Back")
                        }
                    }

                    item {

                        AsyncImage(
                            model = character.imageUrl,
                            contentDescription = character.name,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentScale = ContentScale.Crop
                        )
                    }

                    item {

                        Text(
                            text = character.name,
                            style = MaterialTheme.typography.headlineMedium
                        )
                    }

                    item {

                        Text("Status: ${character.status}")

                        Text("Species: ${character.species}")

                        Text("Gender: ${character.gender}")

                        Text("Origin: ${character.origin}")

                        Text(
                            "Last known location: ${character.location}"
                        )
                    }

                    item {

                        Text(
                            text = "Episodes",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                    items(uiState.episodes) { episode ->

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = episode.episodeCode,
                                style = MaterialTheme.typography.labelLarge
                            )

                            Text(
                                text = episode.name,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }


        }
    }
}