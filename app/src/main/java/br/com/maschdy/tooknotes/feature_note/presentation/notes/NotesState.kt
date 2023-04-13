package br.com.maschdy.tooknotes.feature_note.presentation.notes

import br.com.maschdy.tooknotes.feature_note.domain.model.Note
import br.com.maschdy.tooknotes.feature_note.domain.util.NoteOrder
import br.com.maschdy.tooknotes.feature_note.domain.util.OrderType

data class NotesState(
    val notes: List<Note> = emptyList(),
    val noteOrder: NoteOrder = NoteOrder.Date(OrderType.Descending),
    val isOrderSectionVisible: Boolean = false
)
