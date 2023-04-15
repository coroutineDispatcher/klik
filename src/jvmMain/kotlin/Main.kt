import androidx.compose.runtime.collectAsState
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import game.GameScene

val game = Game()

fun main() = application {
    val gameState = game.gameState.collectAsState()

    when (gameState.value) {
        Game.GlobalGameState.GameOver -> Window(
            title = "Klik",
            onCloseRequest = { this.exitApplication() }) {
            WelcomeScene(game)
        }

        is Game.GlobalGameState.Play -> GameScene(game) {
            exitApplication()
        }
    }
}
