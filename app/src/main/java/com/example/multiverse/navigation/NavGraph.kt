package com.example.multiverse.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.multiverse.data.repository.CharacterRepository
import com.example.multiverse.ui.characterdetail.CharacterDetailScreen
import com.example.multiverse.ui.characterdetail.CharacterDetailViewModel
import com.example.multiverse.ui.characterdetail.CharacterDetailViewModelFactory
import com.example.multiverse.ui.characterlist.CharacterListScreen
import com.example.multiverse.ui.characterlist.CharacterListViewModel
import com.example.multiverse.ui.characterlist.CharacterListViewModelFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    repository: CharacterRepository
) {
    NavHost(
        navController = navController,
        startDestination = "characters"
    ) {

        composable("characters") {

            val listViewModel: CharacterListViewModel =
                viewModel(
                    factory = CharacterListViewModelFactory(
                        repository
                    )
                )

            CharacterListScreen(
                viewModel = listViewModel,
                onCharacterClick = {characterId->
                    navController.navigate("character/$characterId")
                }
            )
        }

        composable("character/{characterId}") { backStackEntry ->

            val characterId =
                backStackEntry.arguments
                    ?.getString("characterId")
                    ?.toIntOrNull()

            if (characterId != null) {

                val detailViewModel: CharacterDetailViewModel =
                    viewModel(
                        factory = CharacterDetailViewModelFactory(
                            repository = repository,
                            characterId = characterId
                        )
                    )

                CharacterDetailScreen(
                    viewModel = detailViewModel,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}