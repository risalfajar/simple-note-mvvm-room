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
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    private lateinit var noteViewModel: NoteViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: NoteAdapter

    companion object {
        const val ADD_NOTE_REQUEST: Int = 1
        const val EDIT_NOTE_REQUEST: Int = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // set up RecyclerView
        viewManager = LinearLayoutManager(this)
        viewAdapter = NoteAdapter()
        recycler_view.apply {
            layoutManager = viewManager
            adapter = viewAdapter

            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)
        }

        // set up FAB
        button_add_note.setOnClickListener(buttonAddNoteListener)

        // set up ViewModel and observe note list data
        noteViewModel =
            ViewModelProvider.AndroidViewModelFactory(application).create(NoteViewModel::class.java).run {
                allNotes.observe(this@DashboardActivity, object : Observer<List<Note>> {
                    override fun onChanged(t: List<Note>?) {
                        if (t != null) viewAdapter.submitList(t)
                        else Toast.makeText(this@DashboardActivity, "No Data", Toast.LENGTH_SHORT).show()
                    }
                })
                this
            }

        // set up swipe behavior for note item
        // dragDirs are 0 because we don't use drag and drop functionality
        // we use swipe left or right
        val itemTouchHelperCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
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

        // set up click behavior for note item
        viewAdapter.setOnItemClickListener(onItemClickListener)
    }

    private val buttonAddNoteListener: View.OnClickListener = object : View.OnClickListener {
        override fun onClick(v: View?) {
            val intent: Intent = Intent(this@DashboardActivity, AddEditNoteActivity::class.java)
            startActivityForResult(intent, ADD_NOTE_REQUEST)
        }
    }

    private val onItemClickListener = object : NoteAdapter.OnItemClickListener {
        override fun onItemClick(note: Note) {
            val intent = Intent(this@DashboardActivity, AddEditNoteActivity::class.java)
            intent
                .putExtra(AddEditNoteActivity.EXTRA_ID, note.id)
                .putExtra(AddEditNoteActivity.EXTRA_TITLE, note.title)
                .putExtra(AddEditNoteActivity.EXTRA_DESCRIPTION, note.description)
                .putExtra(AddEditNoteActivity.EXTRA_PRIORITY, note.priority)
            startActivityForResult(intent, EDIT_NOTE_REQUEST)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ADD_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val title = data?.getStringExtra(AddEditNoteActivity.EXTRA_TITLE) ?: "No data"
                val description = data?.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION) ?: "No data"
                val priority = data?.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1) ?: 1

                // insert data to database
                val note = Note(title, description, priority)
                noteViewModel.insert(note)

                Toast.makeText(this@DashboardActivity, "Note saved", Toast.LENGTH_SHORT).show()
            }
        } else if (requestCode == EDIT_NOTE_REQUEST) {
            if (resultCode == RESULT_OK) {
                val id = data?.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1) ?: -1
                if(id == -1){
                    Toast.makeText(this@DashboardActivity, "Note can't be updated", Toast.LENGTH_SHORT).show()
                    return
                }

                val title = data?.getStringExtra(AddEditNoteActivity.EXTRA_TITLE) ?: "No data"
                val description = data?.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION) ?: "No data"
                val priority = data?.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1) ?: 1

                // update data in database
                val note = Note(title, description, priority).run {
                    this.id = id
                    this
                }
                noteViewModel.update(note)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.delete_all_notes -> {
                noteViewModel.deleteAllNotes()
                Toast.makeText(this@DashboardActivity, "All notes deleted", Toast.LENGTH_SHORT)
                    .show();
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
