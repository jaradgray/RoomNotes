package com.jgendeavors.roomnotes.databases;

import com.jgendeavors.roomnotes.entities.Note;

import java.util.List;

import androidx.room.Dao;

/**
 * The DAO (Data Access Object) provides abstracted access to the Room database
 * We define methods that correspond to database operations, and Room implements the methods
 * under the hood.
 *
 * Rulo of thumb: create one DAO per Room entity
 */
@Dao
public interface NoteDao {
    void insert(Note note);
    void update(Note note);
    void delete(Note note);

    // These methods return all Notes in the database
    List<Note> getNotesByDateModifiedDescending();
    List<Note> getNotesByDateModifiedAscending();
    List<Note> getNotesByDateCreatedDescending();
    List<Note> getNotesByDateCreatedAscending();

    // These methods return Notes with matching category column
    List<Note> getNotesByDateModifiedDescending(String Category);
    List<Note> getNotesByDateModifiedAscending(String Category);
    List<Note> getNotesByDateCreatedDescending(String Category);
    List<Note> getNotesByDateCreatedAscending(String Category);

    // These methods return Notes with matching is_favorited column
    List<Note> getNotesByDateModifiedDescending(boolean isFavorited);
    List<Note> getNotesByDateModifiedAscending(boolean isFavorited);
    List<Note> getNotesByDateCreatedDescending(boolean isFavorited);
    List<Note> getNotesByDateCreatedAscending(boolean isFavorited);
}
