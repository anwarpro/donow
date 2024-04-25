import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import di.initKoin
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    initKoin()
    Napier.base(antilog = DebugAntilog())
    CanvasBasedWindow(canvasElementId = "ComposeTarget") { App() }
}