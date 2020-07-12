package com.jgendeavors.roomnotes.viewmodels;

import android.app.Application;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.repositories.NoteRepository;

import java.util.Calendar;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * The NoteViewModel class holds all Note-related UI data for our app components to observe,
 * and exposes Note manipulation methods for components.
 */
public class NoteViewModel extends AndroidViewModel {

    private NoteRepository mRepository;
    private LiveData<List<Note>> mAllNotes; // all Notes in the database
    private MutableLiveData<Boolean> mIsEditing;
    private MutableLiveData<Note> mNote;    // the Note that drives NoteActivity's UI. This ViewModel doesn't manipulate
                                            // database columns directly (if that's even possible). We keep a local Note
                                            // instance and perform database operations using the entire Note instance.

    public NoteViewModel(@NonNull Application application) {
        super(application);

        mRepository = new NoteRepository(application);
        mAllNotes = mRepository.getAllNotes();
        mIsEditing = new MutableLiveData<>(false); // TODO initialize this better ???
        mNote = new MutableLiveData<>(null);
    }

    // API Methods this ViewModel exposes
    public LiveData<List<Note>> getAllNotes() {
        return mAllNotes;
    }
    public LiveData<Note> getNote() { return mNote; }
    /**
     * When we're dealing with an existing Note, our NoteActivity needs to tell this ViewModel
     * which one it is so we can assign it to mNote.
     * @param id
     */
    public void setNoteById(int id) { mNote.setValue(mRepository.getNote(id)); }
    public void deleteNote() { mRepository.delete(mNote.getValue()); }
    public LiveData<Boolean> getIsEditing() { return mIsEditing; }
    public void setIsEditing(boolean value) { mIsEditing.setValue(value); }
    public void toggleFavorite() {
        Note note = mNote.getValue();
        if (note == null) return;
        note.setIsFavorited(!note.getIsFavorited());
        mRepository.update(note);
    }

    /**
     * Inserts or updates a Note in the database, via the Repository.
     *
     * @param title
     * @param content
     * @param category
     * @param isFavorited
     */
    public void saveNote(String title, String content, String category, boolean isFavorited) {
        Note note;
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (mNote.getValue() == null) {
            // save new Note
            note = new Note(title, content, currentTime, currentTime, category, isFavorited);
            int id = (int)mRepository.insert(note);
            note.setId(id);
        } else {
            // save existing Note if its data has changed
            Note oldNote = mNote.getValue();
            boolean dataNotChanged = oldNote.getTitle().equals(title) && oldNote.getContent().equals(content);
            if (dataNotChanged) {
                return;
            }
            note = new Note(title, content, oldNote.getDateCreated(), currentTime, category, isFavorited);
            note.setId(oldNote.getId());
            mRepository.update(note);
        }
        // update this ViewModel's mNote
        mNote.setValue(note);
    }
}
