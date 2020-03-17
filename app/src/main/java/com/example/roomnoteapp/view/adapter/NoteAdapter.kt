package com.example.roomnoteapp.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.roomnoteapp.R
import com.example.roomnoteapp.service.model.Note

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private var notes: List<Note> = emptyList<Note>()
    private var listener: OnItemClickListener? = null

    // Here where we create and return NoteHolder
    // This is the layout we want to use for the single items in our recyclerview
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)

        return NoteViewHolder(itemView)
    }

    // Here we take care of getting the data from single Note object into
    // the views of our NoteHolder
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        val currentNote: Note = notes.get(position)
        holder.textViewTitle.text = currentNote.title
        holder.textViewDescription.text = currentNote.description
        holder.textViewPriority.text = currentNote.priority.toString()
    }

    override fun getItemCount(): Int = notes.size

    internal fun setNotes(notes: List<Note>){
        this.notes = notes
        notifyDataSetChanged()
    }

    fun getNoteAt(position: Int): Note = notes[position]

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTitle: TextView = itemView.findViewById(R.id.text_view_title)
        val textViewDescription: TextView = itemView.findViewById(R.id.text_view_description)
        val textViewPriority: TextView = itemView.findViewById(R.id.text_view_priority)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if(position != RecyclerView.NO_POSITION)
                    listener?.onItemClick(getNoteAt(position))
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(note: Note)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }
}