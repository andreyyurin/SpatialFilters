package ru.sad.smoothimage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import ru.sad.smoothimage.ui.feature.host.HostScreen
import ru.sad.smoothimage.ui.feature.smooth.MeanFilterScreen
import ru.sad.smoothimage.ui.navigation.HostScreen
import ru.sad.smoothimage.ui.navigation.MeanFilterScreen
import ru.sad.smoothimage.ui.theme.SmoothImageTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SmoothImageTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    App(padding = innerPadding)
                }
            }
        }
    }
}


@Composable
private fun App(
    padding: PaddingValues,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        modifier = Modifier.padding(padding),
        navController = navController,
        startDestination = HostScreen,
    ) {
        composable<HostScreen> {
            HostScreen(navController = navController)
        }
        composable<MeanFilterScreen> {
            MeanFilterScreen()
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
    SmoothImageTheme {
        Greeting("Android")
    }
}