package com.jgendeavors.roomnotes.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * The @Entity annotation tells Room creates an SQLite table for this object
 */
@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    private int mId;
    private String mTitle;
    private String mContent;
    private long mDateCreated; // millis since epoch
    private long mDateModified; // millis since epoch
    private String mCategory;

    // Constructor
    //
    // Room needs to be able to construct Note objects from database data, so we provide a
    // constructor and accessor methods to enable that.
    public Note(String title, String content, long dateCreated, long dateModified, String category) {
        mTitle = title;
        mContent = content;
        mDateCreated = dateCreated;
        mDateModified = dateModified;
        mCategory = category;
    }


    // Getters
    public int getId() { return mId; }
    public String getTitle() { return mTitle; }
    public String getContent() { return mContent; }
    public long getDateCreated() { return mDateCreated; }
    public long getDateModified() { return mDateModified; }
    public String getCategory() { return mCategory; }

    // Setters
    public void setId(int id) { mId = id; }
}
