package game

import Game
import GameViewModel
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

var helperText by mutableStateOf("")
var screenWidth by mutableStateOf(0)
var screenHeight by mutableStateOf(0)
var offset by mutableStateOf(IntOffset(-100, -100))


@Composable
fun GameScene(game: Game, onCloseRequest: () -> Unit) {
    val gameViewModel = GameViewModel()
    val viewModelState = gameViewModel.state.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            gameViewModel.onCleared()
        }
    }

    Window(
        title = "Klik",
        resizable = false,
        onKeyEvent = { keyEvent ->
            if (keyEvent.type == KeyEventType.KeyDown) {
                val letter = keyEvent.key.toString().split(":").last().trim().first()
                helperText += letter
                gameViewModel.check(letter)
                println(helperText)
                true
            } else {
                false
            }
        },
        onCloseRequest = onCloseRequest
    ) {

        LaunchedEffect(viewModelState.value.currentWord) {
            offset = IntOffset(offset.x, -50)
            helperText = ""

            println("Something happening")

            launch {
                while (isActive) {
                    delay(100.milliseconds)
                    offset = IntOffset((screenWidth / 2), offset.y + 10)
                }
            }
        }

        if (viewModelState.value.currentChances.isEmpty()) {
            game.gameOver()
        }

        Box(modifier = Modifier.fillMaxSize().onGloballyPositioned { layoutCoordinates ->
            screenWidth = layoutCoordinates.size.width
            screenHeight = layoutCoordinates.size.height
        }) {

            LazyRow(modifier = Modifier.wrapContentHeight().offset {
                IntOffset(screenWidth - 300, 100)
            }) {
                items(viewModelState.value.currentChances.size) {
                    Text(text = "🍑")
                }
            }

            Text(modifier = Modifier.offset {
                return@offset if ((screenHeight == 0) || (screenWidth == 0)) {
                    IntOffset(-100, -100)
                } else {
                    offset
                }
            }, text = viewModelState.value.currentWord)

            Text(
                modifier = Modifier.width(100.dp).height(100.dp)
                    .offset {
                        IntOffset(screenWidth - 300, screenHeight - 100)
                    }, text = helperText,
                maxLines = 1
            )

            if ((offset.y == (((screenHeight - 100) / 10) * 10)) && screenHeight != 0) {
                offset = IntOffset(offset.x, -50)
                helperText = ""
                gameViewModel.lowerChances()
            }
        }
    }
}
