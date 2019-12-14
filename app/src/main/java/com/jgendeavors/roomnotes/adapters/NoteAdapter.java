package com.jgendeavors.roomnotes.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jgendeavors.roomnotes.R;
import com.jgendeavors.roomnotes.entities.Note;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    /**
     * The ViewHolder class is the View that our RecyclerView will display as its list item,
     * i.e. item_note.xml
     */
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        // TODO adjust instance variables as item_note.xml changes
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvDate;

        public NoteViewHolder(@NonNull View itemView /* the inflated layout for our list item View */) {
            super(itemView);

            // get references to Views in layout
            tvTitle = itemView.findViewById(R.id.item_note_tv_title);
            tvContent = itemView.findViewById(R.id.item_note_tv_content);
            tvDate = itemView.findViewById(R.id.item_note_tv_date);
        }
    }


    // Instance Variables
    private List<Note> mNotes = new ArrayList<Note>();

    
    // Setter

    /**
     * This is how we update our data.
     *
     * @param notes
     */
    public void setNotes(List<Note> notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }


    // Overridden Methods

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(itemView);
    }

    /**
     * Populate View (NoteViewHolder) with data from the corresponding Note object.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = mNotes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        // TODO format date text
        holder.tvDate.setText(String.valueOf(note.getDateCreated()));
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
