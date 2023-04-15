package game

import Chance
import GameDataSource
import ViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.Quadruple
import kotlin.time.Duration

class GameViewModel(
    private val gameRepository: GameRepository = GameRepository(GameDataSource())
) : ViewModel() {
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    data class State(
        val currentWord: String = "",
        val currentChances: List<Chance> = listOf(Chance, Chance, Chance),
        val points: Int = 0,
        val millis: Duration = Duration.ZERO
    )

    init {
        viewModelScope.launch {
            combine(
                gameRepository.wordsFlow,
                gameRepository.chancesFlow,
                gameRepository.pointsFlow,
                gameRepository.milliseconds,
            ) { word, chances, timer, milliseconds ->
                Quadruple(word, chances, timer, milliseconds)
            }.collectLatest { value ->
                _state.update {
                    it.copy(
                        currentWord = value.first,
                        currentChances = value.second,
                        points = value.third,
                        millis = value.forth
                    )
                }
            }

            gameRepository.startTimer()
        }
    }

    fun lowerChances() {
        gameRepository.dropChance()
        gameRepository.next(false)
    }

    fun check(currentLetter: Char) {
        gameRepository.matchCurrentWord(currentLetter)
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
