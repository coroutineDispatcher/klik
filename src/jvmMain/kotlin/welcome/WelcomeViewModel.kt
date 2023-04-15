package welcome

import GameDataSource
import ViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val gameDataSource: GameDataSource = GameDataSource()
) : ViewModel() {
    private val _state = MutableStateFlow<WelcomeState>(WelcomeState.Loading)
    val state: StateFlow<WelcomeState> = _state

    sealed class WelcomeState {
        object Loading : WelcomeState()
        object DataReady : WelcomeState()
    }

    init {
        viewModelScope.launch {
            gameDataSource.prepareData()
                .collectLatest { data ->
                    if (data.isNotEmpty()) {
                        _state.update { WelcomeState.DataReady }
                    }
                }
        }
    }

    override fun onCleared() {
        viewModelScope.cancel()
    }
}
