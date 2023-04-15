import kotlinx.coroutines.flow.flow
import java.io.BufferedReader
import java.io.FileReader

class GameDataSource {
    fun getNewElement(): String = Data.get().random()

    fun prepareData() = flow {
        if (Data.get().isEmpty()) {
            val lines = BufferedReader(FileReader("data/words.txt")).readLines()
            Data.set(lines.map { it.lowercase() })
            emit(lines)
        }
    }
}

object Data {
    private val data = mutableListOf<String>()

    fun set(readData: List<String>) {
        data.addAll(readData)
    }

    fun get(): List<String> = data
}