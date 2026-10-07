package com.example.multiverse.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.multiverse.ui.characterlist.CharacterListScreen
import com.example.multiverse.ui.characterlist.CharacterListViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: CharacterListViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "characters"
    ) {

        composable("characters") {
            CharacterListScreen(
                viewModel = viewModel,
                onCharacterClick = {characterId->
                    navController.navigate("character/$characterId")
                }
            )
        }

        composable("character/{characterId}") { backStackEntry ->

            val characterId =
                backStackEntry.arguments?.getString("characterId")?.toIntOrNull()

            // Detail screen will be added here

            Text("Character Id $characterId")
        }
    }
}