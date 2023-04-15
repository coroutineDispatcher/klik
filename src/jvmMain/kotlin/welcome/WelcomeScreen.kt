import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import welcome.WelcomeViewModel

val viewModel = WelcomeViewModel()

@Composable
fun WelcomeScene(game: Game) {
    val state = viewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose { viewModel.onCleared() }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when (state.value) {
            is WelcomeViewModel.WelcomeState.DataReady -> Button(onClick = { game.play() }) {
                Text("Play")
            }
            WelcomeViewModel.WelcomeState.Loading -> CircularProgressIndicator()
        }
    }
}
