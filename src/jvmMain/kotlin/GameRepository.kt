import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class GameRepository(private val datasource: GameDataSource) {
    private val _wordsFlow =
        MutableSharedFlow<String>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    private val _chancesFlow =
        MutableSharedFlow<List<Chance>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    val wordsFlow = _wordsFlow.asSharedFlow()
    val chancesFlow = _chancesFlow.asSharedFlow()
    private var currentWord = datasource.getNewElement()
    private var currentWordCopy = currentWord
    private val letters = mutableListOf<Char>()
    private val chances = mutableListOf(Chance, Chance, Chance)

    init {
        _wordsFlow.tryEmit(currentWord)
        _chancesFlow.tryEmit(chances)
    }

    fun next() {
        currentWord = datasource.getNewElement()
        currentWordCopy = currentWord
        letters.clear()
        _wordsFlow.tryEmit(currentWord)
    }

    fun matchCurrentWord(currentLetter: Char) {
        println("Current Letter $currentLetter")
        println("Incoming: $currentWordCopy")

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

        println("New word: $newWord")
        println("New word: $currentWord")

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
