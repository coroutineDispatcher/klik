package game

import Chance
import GameDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

class GameRepository(private val datasource: GameDataSource) {
    private val _wordsFlow =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _chancesFlow =
        MutableSharedFlow<List<Chance>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _pointsFlow =
        MutableSharedFlow<Int>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _milliseconds = MutableSharedFlow<Duration>(
        replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val wordsFlow = _wordsFlow.asSharedFlow()
    val chancesFlow = _chancesFlow.asSharedFlow()
    val pointsFlow = _pointsFlow.asSharedFlow()
    val milliseconds = _milliseconds.asSharedFlow()

    private var currentWord = datasource.getNewElement()
    private var currentWordCopy = currentWord
    private val letters = mutableListOf<Char>()
    private val chances = mutableListOf(Chance, Chance, Chance)
    private var points = 0
    private var initialMilliseconds = 100.milliseconds

    init {
        _wordsFlow.tryEmit(currentWord)
        _chancesFlow.tryEmit(chances)
        _pointsFlow.tryEmit(points)
        _milliseconds.tryEmit(initialMilliseconds)
    }

    suspend fun startTimer() = withContext(Dispatchers.Default) {
        while (isActive) {
            if (initialMilliseconds == 10.milliseconds) break
            delay(2.minutes)
            initialMilliseconds -= 10.milliseconds
            _milliseconds.tryEmit(initialMilliseconds)
        }
    }

    fun next(addPoint: Boolean = true) {
        currentWord = datasource.getNewElement()
        currentWordCopy = currentWord
        letters.clear()
        _wordsFlow.tryEmit(currentWord)
        if (addPoint) {
            points += 1
            _pointsFlow.tryEmit(points)
        }
    }

    fun matchCurrentWord(currentLetter: Char) {
        if (currentWordCopy.startsWith(
                currentLetter,
                ignoreCase = true
            ) && currentWordCopy.isNotEmpty()
        ) {
            letters.add(currentLetter)
            currentWordCopy = currentWordCopy.drop(1)
            println("Current Word Copy: $currentWordCopy")
        }

        println(letters.toString())

        val newWord = buildString {
            letters.forEach { append(it.toString()) }
        }

        if ((newWord.equals(currentWord, ignoreCase = true)) && (currentWordCopy.isEmpty())) {
            println("Should go next")
            next()
        }
    }

    fun dropChance() {
        chances.removeFirst()
        _chancesFlow.tryEmit(chances)
    }
}
