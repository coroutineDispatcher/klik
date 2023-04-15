import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val gameRepository: GameRepository = GameRepository(GameDataSource())
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    data class State(
        val currentWord: String = "",
        val currentChances: List<Chance> = listOf(Chance, Chance, Chance)
    )

    init {
        viewModelScope.launch {
            combine(gameRepository.wordsFlow, gameRepository.chancesFlow) { word, chances ->
                Pair(word, chances)
            }.collectLatest { value ->
                _state.update { it.copy(currentWord = value.first, value.second) }
            }
        }
    }

    fun lowerChances() {
        gameRepository.dropChance()
        gameRepository.next()
    }

    fun check(currentLetter: Char) {
        gameRepository.matchCurrentWord(currentLetter)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
