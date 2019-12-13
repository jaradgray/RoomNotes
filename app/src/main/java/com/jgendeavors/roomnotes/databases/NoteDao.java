package com.jgendeavors.roomnotes.databases;

import com.jgendeavors.roomnotes.entities.Note;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

/**
 * The DAO (Data Access Object) provides abstracted access to the Room database
 * We define methods that correspond to database operations, and Room implements the methods
 * under the hood.
 *
 * Rulo of thumb: create one DAO per Room entity
 */
@Dao
public interface NoteDao {
    @Insert
    void insert(Note note);
    @Update
    void update(Note note);
    @Delete
    void delete(Note note);

    // These methods return all Notes in the database
    @Query("SELECT * FROM note_table ORDER BY date_modified DESC")
    LiveData<List<Note>> getNotesByDateModifiedDescending();
    @Query("SELECT * FROM note_table ORDER BY date_modified ASC")
    LiveData<List<Note>> getNotesByDateModifiedAscending();
    @Query("SELECT * FROM note_table ORDER BY date_created DESC")
    LiveData<List<Note>> getNotesByDateCreatedDescending();
    @Query("SELECT * FROM note_table ORDER BY date_created ASC")
    LiveData<List<Note>> getNotesByDateCreatedAscending();

    // These methods return Notes with matching category column
    @Query("SELECT * FROM note_table WHERE category = :category ORDER BY date_modified DESC")
    LiveData<List<Note>> getNotesByDateModifiedDescending(String category);
    @Query("SELECT * FROM note_table WHERE category = :category ORDER BY date_modified ASC")
    LiveData<List<Note>> getNotesByDateModifiedAscending(String category);
    @Query("SELECT * FROM note_table WHERE category = :category ORDER BY date_created DESC")
    LiveData<List<Note>> getNotesByDateCreatedDescending(String category);
    @Query("SELECT * FROM note_table WHERE category = :category ORDER BY date_created ASC")
    LiveData<List<Note>> getNotesByDateCreatedAscending(String category);

    // These methods return Notes with matching is_favorited column
    @Query("SELECT * FROM note_table WHERE is_favorited = :isFavorited ORDER BY date_modified DESC")
    LiveData<List<Note>> getNotesByDateModifiedDescending(boolean isFavorited);
    @Query("SELECT * FROM note_table is_favorited = :isFavorited ORDER BY date_modified ASC")
    LiveData<List<Note>> getNotesByDateModifiedAscending(boolean isFavorited);
    @Query("SELECT * FROM note_table is_favorited = :isFavorited ORDER BY date_created DESC")
    LiveData<List<Note>> getNotesByDateCreatedDescending(boolean isFavorited);
    @Query("SELECT * FROM note_table is_favorited = :isFavorited ORDER BY date_created ASC")
    LiveData<List<Note>> getNotesByDateCreatedAscending(boolean isFavorited);
}
