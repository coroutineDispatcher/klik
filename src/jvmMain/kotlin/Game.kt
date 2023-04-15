import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class Game {
    private val _gameState = MutableStateFlow<GlobalGameState>(GlobalGameState.GameOver)
    val gameState: StateFlow<GlobalGameState> = _gameState

    sealed class GlobalGameState {
        object Play : GlobalGameState()
        object GameOver : GlobalGameState()
    }

    fun play() {
        _gameState.tryEmit(GlobalGameState.Play)
    }

    fun gameOver() {
        _gameState.tryEmit(GlobalGameState.GameOver)
    }
}