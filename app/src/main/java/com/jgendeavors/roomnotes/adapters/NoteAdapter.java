package com.jgendeavors.roomnotes.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgendeavors.roomnotes.R;
import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.util.Util;

import java.util.ArrayList;
import java.util.Calendar;
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
        public ImageView ivFavorited;
        public TextView tvTitle;
        public TextView tvContent;
        public TextView tvDate;

        public NoteViewHolder(@NonNull View itemView /* the inflated layout for our list item View */) {
            super(itemView);

            // get references to Views in layout
            ivFavorited = itemView.findViewById(R.id.item_note_iv_favorited);
            tvTitle = itemView.findViewById(R.id.item_note_tv_title);
            tvContent = itemView.findViewById(R.id.item_note_tv_content);
            tvDate = itemView.findViewById(R.id.item_note_tv_date);

            // Capture clicks on this item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        // notify mListener
                        int position = getAdapterPosition();
                        mListener.onItemClicked(mNotes.get(position));
                    }
                }
            });
        }
    }

    /**
     * The interface that reports clicks on items in this Adapter's RecyclerView
     */
    public interface OnItemClickListener {
        void onItemClicked(Note note);
    }
    private OnItemClickListener mListener;
    public void setOnItemClickListener(OnItemClickListener listener) { mListener = listener; }


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
     * Populate item View (NoteViewHolder) with data from the corresponding Note object.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = mNotes.get(position);

        holder.tvTitle.setText(note.getTitle());
        holder.tvContent.setText(note.getContent());
        // Set tvDate's text via format String resource and utility method.
        // To do that we need a Context reference, which we can get from any View object
        Context context = holder.tvDate.getContext();
        String dateLastModified = Util.getTimeAsString(context, note.getDateModified(), Calendar.SHORT);
        String dateCreated = Util.getTimeAsString(context, note.getDateCreated(), Calendar.SHORT);
        String dateText = context.getString(R.string.note_adapter_date_format,
                dateLastModified, dateCreated);
        holder.tvDate.setText(dateText);

        // set ivFavorited's visibility based on note.getIsFavorited
        int visibility = (note.getIsFavorited()) ? View.VISIBLE : View.GONE;
        holder.ivFavorited.setVisibility(visibility);
    }

    @Override
    public int getItemCount() {
        return mNotes.size();
    }
}
