package com.jgendeavors.roomnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

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

        // Request a NoteViewModel from the Android system
        mNoteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);

        // observe the ViewModel's LiveData
        mNoteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                // called each time the data in the LiveData object changes
                // TODO update UI
            }
        });
    }
}
