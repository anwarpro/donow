package ui.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.jan.supabase.annotations.SupabaseExperimental
import moe.tlaster.precompose.flow.collectAsStateWithLifecycle
import moe.tlaster.precompose.koin.koinViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier
) {
    val homeViewModel: HomeViewModel = koinViewModel(vmClass = HomeViewModel::class)
    val state by homeViewModel.state.collectAsStateWithLifecycle()

    HomeScreenContent(
        modifier = modifier,
        state = state
    ) {
        homeViewModel.postAction(it)
    }
}

@OptIn(ExperimentalMaterial3Api::class, SupabaseExperimental::class)
@Composable
fun HomeScreenContent(
    modifier: Modifier = Modifier,
    state: HomeState,
    postAction: (HomeAction) -> Unit
) {
    var showAddForm by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showAddForm = true
                }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
        floatingActionButtonPosition = FabPosition.EndOverlay
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(it)) {
            state.tasks?.items?.forEach {
                Text(it.title)
            }
        }
    }

    if (showAddForm) {
        ModalBottomSheet(
            onDismissRequest = {
                showAddForm = false
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxWidth()
        ) {

            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }

            Column(
                modifier = Modifier.fillMaxWidth().wrapContentHeight()
                    .padding(vertical = 16.dp, horizontal = 16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    label = {
                        Text("Title")
                    },
                    onValueChange = {
                        title = it
                    }
                )
                OutlinedTextField(
                    value = description,
                    label = {
                        Text("Description")
                    },
                    onValueChange = {
                        description = it
                    }
                )
                Row(modifier = Modifier.padding(vertical = 24.dp)) {
                    TextButton(
                        onClick = {
                            showAddForm = false
                        }
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = {
                            postAction(
                                HomeAction.SaveTask(
                                    title = title,
                                    description = description
                                )
                            )
                        }
                    ) {
                        Text(text = "Save")
                    }
                }
            }
        }
    }
}