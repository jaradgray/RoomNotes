package com.jgendeavors.roomnotes.viewmodels;

import com.jgendeavors.roomnotes.entities.Note;

import androidx.lifecycle.ViewModel;

/**
 * The NoteActivityViewModel stores and processes data that affects the UI of NoteActivity,
 * and communicates user interaction to the model (Repository)
 */
public class NoteActivityViewModel extends ViewModel {


    // API Methods this ViewModel exposes
    public void insert(Note note) {}
    public void update(Note note) {}
}
