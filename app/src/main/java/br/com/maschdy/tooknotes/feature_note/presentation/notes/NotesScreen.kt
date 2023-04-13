package br.com.maschdy.tooknotes.feature_note.presentation.notes

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import br.com.maschdy.tooknotes.feature_note.domain.model.Note
import br.com.maschdy.tooknotes.feature_note.domain.util.NoteOrder
import br.com.maschdy.tooknotes.feature_note.presentation.notes.components.NoteItem
import br.com.maschdy.tooknotes.feature_note.presentation.notes.components.OrderSection
import br.com.maschdy.tooknotes.feature_note.presentation.util.Screen
import br.com.maschdy.tooknotes.ui.theme.BabyBlue
import br.com.maschdy.tooknotes.ui.theme.TookNotesTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    navController: NavController = rememberNavController(),
    viewModel: NotesViewModel = hiltViewModel()
) {
    val state = viewModel.state.value
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate(Screen.AddEditNoteScreen.route)
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add note")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        NotesContent(
            modifier = Modifier
                .padding(innerPadding),
            state = state,
            onNoteClick = { note ->
                navController.navigate(
                    Screen.AddEditNoteScreen.route +
                            "?noteId=${note.id}&noteColor=${note.color}"
                )
            },
            onToggleOrderSectionClick = {
                viewModel.onEvent(NotesEvent.ToggleOrderSection)
            },
            onOrderChange = { noteOrder ->
                viewModel.onEvent(NotesEvent.Order(noteOrder))
            },
            onDeleteClick = { note ->
                viewModel.onEvent(NotesEvent.DeleteNote(note))
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Note deleted",
                        actionLabel = "Undo"
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        viewModel.onEvent(NotesEvent.RestoreNote)
                    }
                }
            }
        )
    }
}

@Composable
private fun NotesContent(
    modifier: Modifier = Modifier,
    state: NotesState,
    onNoteClick: (note: Note) -> Unit,
    onToggleOrderSectionClick: () -> Unit,
    onOrderChange: (noteOrder: NoteOrder) -> Unit,
    onDeleteClick: (note: Note) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Took notes",
                style = MaterialTheme.typography.headlineLarge
            )
            IconButton(
                onClick = { onToggleOrderSectionClick() }
            ) {
                Icon(
                    imageVector = Icons.Default.Sort,
                    contentDescription = "Sort"
                )
            }
        }
        AnimatedVisibility(
            visible = state.isOrderSectionVisible,
            enter = fadeIn() + slideInVertically(),
            exit = fadeOut() + slideOutVertically()
        ) {
            OrderSection(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                noteOrder = state.noteOrder,
                onOrderChange = { noteOrder ->
                    onOrderChange(noteOrder)
                }
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(Modifier.fillMaxSize()) {
            items(state.notes) { note ->
                NoteItem(
                    note = note,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNoteClick(note) },
                    onDeleteClick = { onDeleteClick(note) }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Preview
@Composable
private fun NotesScreenPreview() {
    TookNotesTheme {
        Surface {
            val state = NotesState(
                notes = listOf(
                    Note(
                        title = "Note 1",
                        content = "Content 1",
                        timestamp = System.currentTimeMillis(),
                        color = BabyBlue.toArgb()
                    )
                )
            )
            NotesContent(
                state = state,
                onNoteClick = {},
                onToggleOrderSectionClick = {},
                onOrderChange = {},
                onDeleteClick = {}
            )
        }
    }
}
