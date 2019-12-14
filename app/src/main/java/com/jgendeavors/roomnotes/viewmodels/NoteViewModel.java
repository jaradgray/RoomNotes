package com.jgendeavors.roomnotes.viewmodels;

import android.app.Application;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.repositories.NoteRepository;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

/**
 * The ViewModel class is responsible for storing and processing data that affects the UI, and
 * for communicating user interaction to the model (Repository)
 *
 * Data stored in the ViewModel survives configuration changes, unlike data stored in an Activity or Fragment
 *
 * Remember, ViewModel outlives Activities and Fragments, so don't store references to Activity Contexts
 * or to Views that reference the Activity Context in the ViewModel. We need to pass an Application
 * (Context subclass) to our Repository, so we extend AndroidViewModel whose constructor provides one.
 *
 * Interactions:
 *  - Incoming:
 *      - MainActivity
 *      - NoteActivity
 *  - Outgoing:
 *      - NoteRepository
 */
public class NoteViewModel extends AndroidViewModel {
    // Instance Variables
    private NoteRepository mRepository;
    private LiveData<List<Note>> mAllNotes;

    // Constructor
    public NoteViewModel(@NonNull Application application) {
        super(application);

        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNotes();
    }


    // Data manipulation methods
    // Our Activity only has access to the ViewModel and not the Repository, so we create
    // wrapper methods for the Repository API

    public void insert(Note note) {
        mRepository.insert(note);
    }

    public void update(Note note) {
        mRepository.update(note);
    }

    public void delete(Note note) {
        mRepository.delete(note);
    }

    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }
}
