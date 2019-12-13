package com.jgendeavors.roomnotes.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * The @Entity annotation tells Room creates an SQLite table for this object
 */
@Entity(tableName = "note_table")
public class Note {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int mId;
    @ColumnInfo(name = "title")
    private String mTitle;
    @ColumnInfo(name = "content")
    private String mContent;
    @ColumnInfo(name = "date_created")
    private long mDateCreated; // millis since epoch
    @ColumnInfo(name = "date_modified")
    private long mDateModified; // millis since epoch
    @ColumnInfo(name = "category")
    private String mCategory;
    @ColumnInfo(name = "is_favorited")
    private boolean mIsFavorited;

    // Constructor
    //
    // Room needs to be able to construct Note objects from database data, so we provide a
    // constructor and accessor methods to enable that.
    public Note(String title, String content, long dateCreated, long dateModified, String category, boolean isFavorited) {
        mTitle = title;
        mContent = content;
        mDateCreated = dateCreated;
        mDateModified = dateModified;
        mCategory = category;
        mIsFavorited = isFavorited;
    }


    // Getters
    public int getId() { return mId; }
    public String getTitle() { return mTitle; }
    public String getContent() { return mContent; }
    public long getDateCreated() { return mDateCreated; }
    public long getDateModified() { return mDateModified; }
    public String getCategory() { return mCategory; }
    public boolean getIsFavorited() { return mIsFavorited; }

    // Setters
    public void setId(int id) { mId = id; }
}
