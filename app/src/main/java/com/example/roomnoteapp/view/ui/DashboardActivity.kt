package com.example.roomnoteapp.view.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roomnoteapp.R
import com.example.roomnoteapp.service.model.Note
import com.example.roomnoteapp.view.adapter.NoteAdapter
import com.example.roomnoteapp.viewmodel.NoteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class DashboardActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: NoteAdapter

    companion object{
        val ADD_NOTE_REQUEST: Int = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val buttonAddNote: FloatingActionButton = findViewById(R.id.button_add_note)
        buttonAddNote.setOnClickListener(buttonAddNoteListener)

        viewManager = LinearLayoutManager(this)
        viewAdapter = NoteAdapter()

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = viewManager
            adapter = viewAdapter

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
        }

        noteViewModel = ViewModelProvider.AndroidViewModelFactory(application).create(NoteViewModel::class.java)
        noteViewModel.allNotes.observe(this, object: Observer<List<Note>> {
            override fun onChanged(t: List<Note>?) {
                if(t != null) viewAdapter.setNotes(t)
                else Toast.makeText(this@DashboardActivity, "No Data", Toast.LENGTH_SHORT).show()
            }
        })

        // dragDirs are 0 because we don't use drag and drop functionality
        // we use swipe left or right
        val itemTouchHelperCallback = object: ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // what position in adapter the user swiped at
                val position: Int = viewHolder.adapterPosition
                noteViewModel.delete(viewAdapter.getNoteAt(position))
                Toast.makeText(this@DashboardActivity, "Note deleted", Toast.LENGTH_SHORT).show()
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    private val buttonAddNoteListener: View.OnClickListener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            val intent: Intent = Intent(this@DashboardActivity, AddNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == ADD_NOTE_REQUEST){
            if(resultCode == RESULT_OK){
                val title = data?.getStringExtra(AddNoteActivity.EXTRA_TITLE) ?: "No data"
                val description = data?.getStringExtra(AddNoteActivity.EXTRA_DESCRIPTION) ?: "No data"
                val priority = data?.getIntExtra(AddNoteActivity.EXTRA_PRIORITY, 1) ?: 1

                // insert data to database
                val note = Note(title, description, priority)
                noteViewModel.insert(note)

                Toast.makeText(this@DashboardActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete_all_notes -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this@DashboardActivity, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
