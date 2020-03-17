package com.example.roomnoteapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.roomnoteapp.service.model.Note
import com.example.roomnoteapp.service.repository.NoteRepository
import kotlinx.coroutines.launch

class NoteViewModel(application: Application) : AndroidViewModel(application) {
    private var repository: NoteRepository
    var allNotes: LiveData<List<Note>>
        private set

    init {
        repository = NoteRepository(application, viewModelScope)
        allNotes = repository.allNotes
    }

    fun insert(note: Note) = viewModelScope.launch{
        repository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch{
        repository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch{
        repository.delete(note)
    }

    fun deleteAllNotes() = viewModelScope.launch{
        repository.deleteAllNotes()
    }
}