package com.jgendeavors.roomnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jgendeavors.roomnotes.adapters.NoteAdapter;
import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.viewmodels.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Instance Variables
    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize RecyclerView
        RecyclerView recyclerView = findViewById(R.id.activity_main_recyclerview);
        // Every RecyclerView needs a LayoutManager. Our RecyclerView will display items
        // in a vertical list, so we need a LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true); // improves performance when we know the size of our RecyclerView in the layout won't change
        // Every RecyclerView needs a RecyclerView.Adapter. We'll need a reference to it, too.
        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        // Set up FAB
        FloatingActionButton fab = findViewById(R.id.activity_main_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Start NoteActivity
                Intent intent = new Intent(MainActivity.this, NoteActivity.class);
                startActivity(intent);
            }
        });

        // Request a NoteViewModel from the Android system
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // observe the ViewModel's LiveData
        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            /**
             * Called each time the data in the LiveData object we're observing changes.
             * @param notes
             */
            @Override
            public void onChanged(List<Note> notes) {
                // update RecyclerView UI
                adapter.setNotes(notes);
            }
        });
    }
}
