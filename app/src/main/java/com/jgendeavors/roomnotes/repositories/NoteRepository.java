package com.jgendeavors.roomnotes.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.jgendeavors.roomnotes.databases.NoteDao;
import com.jgendeavors.roomnotes.databases.NoteDatabase;
import com.jgendeavors.roomnotes.entities.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

/**
 * The Repository class is in the Android recommended architecture.
 * It provides a single point of access to potentially more than one data source.
 * However, in this app we have only one data source (the Room database)
 *
 * The Repository provides our ViewModel with a clean API for accessing our app's data
 *
 * Interactions:
 *  - Incoming:
 *      - NoteViewModel
 *  - Outgoing:
 *      - NoteDatabase
 *      - NoteDao
 */
public class NoteRepository {
    // Instance Variables
    private NoteDao noteDao; // for database access, the Repository only interacts with the DAO
    private LiveData<List<Note>> allNotes;

    // Constructor
    public NoteRepository(Application application) {
        NoteDatabase database = NoteDatabase.getInstance(application);
        noteDao = database.noteDao();
        allNotes = noteDao.getAllNotes(); // Room automatically executes database operations that return LiveData on a background thread
    }


    // Data manipulation methods
    // These are the API methods the Repository exposes to the outside
    // Remember, database accesses must be performed on a background thread

    public long insert(Note note) {
        long result = -1;
        try {
            result = new InsertNoteAsyncTask(noteDao).execute(note).get();
        } catch (Exception e) { System.err.println("NoteRepository.insert(Note) Error: " + e); }
        return result;
    }

    public void update(Note note) {
        new UpdateNoteAsyncTask(noteDao).execute(note);
    }

    public void delete(Note note) {
        new DeleteNoteAsyncTask(noteDao).execute(note);
    }

    public Note getNote(int id) {
        Note result = null;
        try {
            result = new GetNoteAsyncTask(noteDao).execute(id).get();
        } catch (Exception e) { System.err.println("NoteRepository.getNote(int) Error: " + e); }
        return result;
    }

    public LiveData<List<Note>> getAllNotes() {
        return allNotes;
    }


    // AsyncTasks for performing database operations on a background thread
    // Note: they are static so they don't have a reference to the Repository itself, which could create memory leaks

    private static class InsertNoteAsyncTask extends AsyncTask<Note, Void, Long> {
        // Instance Variables
        private NoteDao noteDao; // since this AsyncTask is static, it doesn't have access to the repository's DAO

        // Constructor
        public InsertNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        // Overridden methods
        @Override
        protected Long doInBackground(Note... notes) {
            return noteDao.insert(notes[0]);
        }
    }

    private static class UpdateNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        // Instance Variables
        private NoteDao noteDao; // since this AsyncTask is static, it doesn't have access to the repository's DAO

        // Constructor
        public UpdateNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        // Overridden methods
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.update(notes[0]);
            return null;
        }
    }

    private static class DeleteNoteAsyncTask extends AsyncTask<Note, Void, Void> {
        // Instance Variables
        private NoteDao noteDao; // since this AsyncTask is static, it doesn't have access to the repository's DAO

        // Constructor
        public DeleteNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        // Overridden methods
        @Override
        protected Void doInBackground(Note... notes) {
            noteDao.delete(notes[0]);
            return null;
        }
    }

    private static class GetNoteAsyncTask extends AsyncTask<Integer, Void, Note> {
        // Instance Variables
        private NoteDao noteDao; // since this AsyncTask is static, it doesn't have access to the repository's DAO

        // Constructor
        public GetNoteAsyncTask(NoteDao noteDao) {
            this.noteDao = noteDao;
        }

        // Overridden methods
        @Override
        protected Note doInBackground(Integer... integers) {
            return noteDao.getNote(integers[0]);
        }
    }
}
