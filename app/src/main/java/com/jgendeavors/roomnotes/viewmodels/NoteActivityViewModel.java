package com.jgendeavors.roomnotes.viewmodels;

import android.app.Application;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.repositories.NoteRepository;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * The NoteActivityViewModel stores and processes data that affects the UI of NoteActivity,
 * and communicates user interaction to the model (Repository)
 */
public class NoteActivityViewModel extends AndroidViewModel {

    private NoteRepository mRepository;
    private MutableLiveData<Boolean> mIsEditing;

    public NoteActivityViewModel(@NonNull Application application) {
        super(application);

        mRepository = new NoteRepository(application);
        mIsEditing = new MutableLiveData<>(false); // TODO initialize this better ???
    }

    // API Methods this ViewModel exposes
    public void insert(Note note) { mRepository.insert(note); }
    public void update(Note note) { mRepository.update(note); }
    public LiveData<Boolean> getIsEditing() { return mIsEditing; }
    public void setIsEditing(boolean value) { mIsEditing.setValue(value); }
}
