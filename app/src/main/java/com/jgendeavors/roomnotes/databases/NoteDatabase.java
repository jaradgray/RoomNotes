package com.jgendeavors.roomnotes.databases;

import android.content.Context;

import com.jgendeavors.roomnotes.entities.Note;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

/**
 * Singleton pattern
 */
@Database(entities = {Note.class}, version = 1)
public abstract class NoteDatabase extends RoomDatabase {
    private static NoteDatabase instance;

    public abstract NoteDao noteDao(); // Room implements this method under the hood with the databaseBuilder() method

    public static synchronized NoteDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    NoteDatabase.class, "note_database")
                    .fallbackToDestructiveMigration() // delete this database when version number is incremented
                    .build();
        }
        return instance;
    }
}
