package com.example.multiverse.ui.characterlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems

@Composable
fun CharacterListScreen(
    viewModel: CharacterListViewModel
) {

    val characters = viewModel.characters.collectAsLazyPagingItems()



    if (
        characters.loadState.refresh is LoadState.NotLoading &&
        characters.itemCount == 0
    ) {
        Text(
            text = "No characters found"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // Search + filters will go here

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            when (val appendState = characters.loadState.append) {

                is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator()
                    }
                }

                is LoadState.Error -> {
                    item {
                        Text(
                            text = "Failed to load more. Tap to retry."
                        )
                    }
                }

                is LoadState.NotLoading -> Unit
            }

            items(
                count = characters.itemCount
            ) { index ->

                val character = characters[index]

                if (character != null) {
                    Text(
                        text = character.name
                    )
                }
            }

            if (characters.loadState.append
                        is androidx.paging.LoadState.Loading) {

                item {
                    CircularProgressIndicator()
                }
            }
        }
    }
}