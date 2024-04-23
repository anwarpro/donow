import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import io.github.jan.supabase.gotrue.SessionStatus
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.koin.koinViewModel
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.navigation.transition.NavTransition
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App() {
    PreComposeApp {
        val navigator = rememberNavigator()

        MaterialTheme {

            val appViewModel = koinViewModel(vmClass = AppViewModel::class)
            val sessionStatus by appViewModel.sessionStatus.collectAsState()

            val initialRoute = when (sessionStatus) {
                is SessionStatus.Authenticated -> "/home"
                is SessionStatus.NotAuthenticated -> "/login"
                else -> {
                    "/splash"
                }
            }

            NavHost(
                // Assign the navigator to the NavHost
                navigator = navigator,
                // Navigation transition for the scenes in this NavHost, this is optional
                navTransition = NavTransition(),
                // The start destination
                initialRoute = initialRoute,
            ) {
                // Define a scene to the navigation graph
                scene(
                    // Scene's route path
                    route = "/home",
                    // Navigation transition for this scene, this is optional
                    navTransition = NavTransition(),
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Home")
                    }
                }

                scene(
                    route = "/login",
                    navTransition = NavTransition()
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text("Login")
                    }
                }

                scene(
                    route = "/splash",
                    navTransition = NavTransition()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
        }
    }
}