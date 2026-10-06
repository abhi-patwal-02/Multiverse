package com.example.multiverse.ui.characterlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil3.compose.AsyncImage
import com.example.multiverse.domain.model.CharacterModel

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel
) {
    val characters = viewModel.characters.collectAsLazyPagingItems()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val status by viewModel.status.collectAsState()
    val gender by viewModel.gender.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {

        // Search
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                viewModel.updateSearchQuery(it)
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search characters...")
            },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Status
        Text(
            text = "Status",
            style = MaterialTheme.typography.labelLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                FilterChip(
                    selected = status == null,
                    onClick = {
                        viewModel.updateStatus(null)
                    },
                    label = {
                        Text("All")
                    }
                )
            }

            items(
                listOf(
                    "Alive",
                    "Dead",
                    "Unknown"
                )
            ) { value ->

                FilterChip(
                    selected = status == value,
                    onClick = {
                        viewModel.updateStatus(
                            if (status == value) null else value
                        )
                    },
                    label = {
                        Text(value)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Gender
        Text(
            text = "Gender",
            style = MaterialTheme.typography.labelLarge
        )

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            item {
                FilterChip(
                    selected = gender == null,
                    onClick = {
                        viewModel.updateGender(null)
                    },
                    label = {
                        Text("All")
                    }
                )
            }

            items(
                listOf(
                    "Female",
                    "Male",
                    "Genderless",
                    "Unknown"
                )
            ) { value ->

                FilterChip(
                    selected = gender == value,
                    onClick = {
                        viewModel.updateGender(
                            if (gender == value) null else value
                        )
                    },
                    label = {
                        Text(value)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main content
        when (characters.loadState.refresh) {

            // First page loading
            is LoadState.Loading -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            // First page error
            is LoadState.Error -> {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        Text(
                            text = "Something went wrong"
                        )

                        Spacer(
                            modifier = Modifier.height(8.dp)
                        )

                        Button(
                            onClick = {
                                characters.retry()
                            }
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }

            // First page successfully loaded
            is LoadState.NotLoading -> {

                // Empty result
                if (characters.itemCount == 0) {

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No characters found")
                    }

                } else {

                    // Character list
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {

                        items(
                            count = characters.itemCount
                        ) { index ->

                            val character = characters[index]

                            if (character != null) {

                                CharacterCard(
                                    character = character,
                                    onClick = {
                                        // Navigation will be added later
                                    }
                                )
                            }
                        }

                        // Next page state
                        when (characters.loadState.append) {

                            is LoadState.Loading -> {

                                item {

                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            is LoadState.Error -> {

                                item {

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalAlignment =
                                            Alignment.CenterHorizontally
                                    ) {

                                        Text(
                                            text = "Couldn't load more characters"
                                        )

                                        Spacer(
                                            modifier = Modifier.height(8.dp)
                                        )

                                        Button(
                                            onClick = {
                                                characters.retry()
                                            }
                                        ) {
                                            Text("Retry")
                                        }
                                    }
                                }
                            }

                            is LoadState.NotLoading -> Unit
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CharacterCard(
    character: CharacterModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {

        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            AsyncImage(
                model = character.imageUrl,
                contentDescription = character.name,
                modifier = Modifier
                    .size(80.dp)
                    .clip(
                        RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.Crop
            )

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column {

                Text(
                    text = character.name,
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    StatusIndicator(
                        status = character.status
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = character.status,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = character.species,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun StatusIndicator(
    status: String
) {
    val indicatorColor = when (status.lowercase()) {
        "alive" -> Color.Green
        "dead" -> Color.Red
        else -> Color.Gray
    }

    Box(
        modifier = Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(indicatorColor)
    )
}