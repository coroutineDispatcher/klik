import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

abstract class ViewModel {
    private val job = SupervisorJob()
    val viewModelScope = CoroutineScope(Dispatchers.Default + job)
    abstract fun onCleared()
}
