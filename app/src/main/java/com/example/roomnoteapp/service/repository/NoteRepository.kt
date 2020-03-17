package com.example.roomnoteapp.service.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.roomnoteapp.service.dao.NoteDao
import com.example.roomnoteapp.service.database.NoteDatabase
import com.example.roomnoteapp.service.model.Note
import kotlinx.coroutines.CoroutineScope

class NoteRepository(application: Application, scope: CoroutineScope) {
    private var noteDao: NoteDao
    var allNotes: LiveData<List<Note>>
        private set

    init{
        val database: NoteDatabase =
            NoteDatabase.getInstance(
                application,
                scope
            )
        noteDao = database.noteDao()
        allNotes = noteDao.getAllNotes()
    }

    suspend fun insert(note: Note){
        noteDao.insert(note)
    }

    suspend fun update(note: Note){
        noteDao.update(note)
    }

    suspend fun delete(note: Note){
        noteDao.delete(note)
    }

    suspend fun deleteAllNotes(){
        noteDao.deleteAllNotes()
    }
}