package ru.sad.smoothimage.ui.feature.host

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import ru.sad.smoothimage.ui.navigation.MeanFilterScreen

@Composable
fun HostScreen(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        TextButton(
            modifier = Modifier
                .background(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.Black
                ),
            onClick = {
                navController.navigate(MeanFilterScreen)
            }
        ) {
            Text(
                text = "Mean filter",
                color = Color.White
            )
        }
    }
}