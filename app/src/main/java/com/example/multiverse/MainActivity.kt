package com.example.multiverse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.multiverse.di.AppContainer
import com.example.multiverse.navigation.NavGraph
import com.example.multiverse.ui.characterlist.CharacterListScreen
import com.example.multiverse.ui.characterlist.CharacterListViewModel
import com.example.multiverse.ui.characterlist.CharacterListViewModelFactory
import com.example.multiverse.ui.theme.MultiverseTheme

class MainActivity : ComponentActivity() {

    private val appContainer by lazy {
        AppContainer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {



            val navController = rememberNavController()

            MultiverseTheme {
                NavGraph(
                    navController = navController,
                    repository = appContainer.characterRepository
                )
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MultiverseTheme {
        Greeting("Android")
    }
}