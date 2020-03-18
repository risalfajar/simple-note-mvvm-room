package com.example.roomnoteapp.view.ui

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import com.example.roomnoteapp.R
import kotlinx.android.synthetic.main.activity_add_note.*

class AddEditNoteActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_TITLE: String = "com.example.roomnoteapp.view.ui.EXTRA_TITLE"
        const val EXTRA_DESCRIPTION: String = "com.example.roomnoteapp.view.ui.EXTRA_DESCRIPTION"
        const val EXTRA_PRIORITY: String = "com.example.roomnoteapp.view.ui.EXTRA_PRIORITY"
        const val EXTRA_ID: String = "com.example.roomnoteapp.view.ui.EXTRA_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        number_picker_priority.apply {
            minValue = 1
            maxValue = 10
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)

        // handle intent for add or edit mode
        if(intent.hasExtra(EXTRA_ID)){ // edit mode
            title = "Edit note"
            edit_text_title.setText(intent.getStringExtra(EXTRA_TITLE))
            edit_text_description.setText(intent.getStringExtra(EXTRA_DESCRIPTION))
            number_picker_priority.value = intent.getIntExtra(EXTRA_PRIORITY, 1)
        }
        else title = "Add note" // add mode
    }

    private fun saveNote(){
        val title: String = edit_text_title.text.toString().trim()
        val description: String = edit_text_description.text.toString().trim()
        val priority: Int = number_picker_priority.value

        if(title.isEmpty() || description.isEmpty()){
            Toast.makeText(this@AddEditNoteActivity, "Pleas insert a title and description",
                Toast.LENGTH_SHORT).show()
            return
        }

        // set up returned data for startActivityForResult
        val returnedData = Intent()
        returnedData
            .putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_DESCRIPTION, description)
            .putExtra(EXTRA_PRIORITY, priority)

        // if it is edit mode, include note ID in returnedData
        intent.getIntExtra(EXTRA_ID, -1).let {
            if(it != -1) returnedData.putExtra(EXTRA_ID, it)
        }

        setResult(Activity.RESULT_OK, returnedData)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_add_note, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId){
            R.id.save_note -> {
                saveNote()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
