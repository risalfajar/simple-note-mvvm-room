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

class AddNoteActivity : AppCompatActivity() {

    companion object{
        val EXTRA_TITLE: String = "com.example.roomnoteapp.view.ui.EXTRA_TITLE"
        val EXTRA_DESCRIPTION: String = "com.example.roomnoteapp.view.ui.EXTRA_DESCRIPTION"
        val EXTRA_PRIORITY: String = "com.example.roomnoteapp.view.ui.EXTRA_PRIORITY"
    }

    private lateinit var editTextTitle: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var numberPickerPriority: NumberPicker

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)

        editTextTitle = findViewById(R.id.edit_text_title)
        editTextDescription = findViewById(R.id.edit_text_description)
        numberPickerPriority = findViewById(R.id.number_picker_priority)

        numberPickerPriority.apply {
            minValue = 1
            maxValue = 10
        }

        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        setTitle("Add Note")
    }

    private fun saveNote(){
        val title: String = editTextTitle.text.toString().trim()
        val description: String = editTextDescription.text.toString().trim()
        val priority: Int = numberPickerPriority.value

        if(title.isEmpty() || description.isEmpty()){
            Toast.makeText(this@AddNoteActivity, "Pleas insert a title and description",
                Toast.LENGTH_SHORT).show()
            return
        }

        var data: Intent = Intent()
        data.putExtra(EXTRA_TITLE, title)
            .putExtra(EXTRA_DESCRIPTION, description)
            .putExtra(EXTRA_PRIORITY, priority)

        setResult(Activity.RESULT_OK, data)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater: MenuInflater = getMenuInflater()
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
