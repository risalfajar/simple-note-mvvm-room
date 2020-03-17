package com.example.roomnoteapp.service.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomnoteapp.service.dao.NoteDao
import com.example.roomnoteapp.service.model.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(entities = arrayOf(Note::class), version = 1)
abstract class NoteDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    //Create private static variable and instance getter for singleton class
    companion object{
        @Volatile private var instance: NoteDatabase? = null

        //if parameter before ?: symbol is not null, return it
        //otherwise return the right expression
        fun getInstance(context: Context, scope: CoroutineScope) =
            instance
                ?: synchronized(this){
                instance
                    ?: Room.databaseBuilder(context.applicationContext,
                    NoteDatabase::class.java, "note_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(NoteDatabaseCallback(scope))
                    .build()
                    .also { instance = it }
            }
    }
    
    // populate database with sample data the first time the app is launched
    private class NoteDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback(){
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            instance?.let {
                scope.launch {
                    populateDatabase(it.noteDao())
                }
            }
        }

        suspend fun populateDatabase(noteDao: NoteDao){
            noteDao.insert(Note("Sample 1", "This is a sample note", 1))
            noteDao.insert(Note("Sample 2", "This is a sample note", 2))
            noteDao.insert(Note("Sample 3", "This is a sample note", 3))
        }
    }
}