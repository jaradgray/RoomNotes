package com.jgendeavors.roomnotes;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.viewmodels.NoteActivityViewModel;

import java.util.Calendar;

public class NoteActivity extends AppCompatActivity {
    // Intent Extras
    public static final String EXTRA_TITLE = "com.jgendeavors.roomnotes.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.jgendeavors.roomnotes.EXTRA_CONTENT";

    // References to Views
    private EditText mEtTitle;
    private EditText mEtContent;

    private NoteActivityViewModel mViewModel;
    private int mOptionsMenuResourceId;
    private boolean mIsEditing;


    // Overridden Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Get references to Views
        mEtTitle = findViewById(R.id.activity_note_et_title);
        mEtContent = findViewById(R.id.activity_note_et_content);

        // TODO the mIsEditing flag should probably be handled in a ViewModel, right?

        // Create the ViewModel that will drive this Activity's UI
        mViewModel = ViewModelProviders.of(this).get(NoteActivityViewModel.class);

        // observe changes to the isEditing state
        mViewModel.getIsEditing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEditing) {
                // TODO change options menu resource
                mOptionsMenuResourceId = isEditing ? R.menu.activity_note_editing_menu : R.menu.activity_note_normal_menu;
                invalidateOptionsMenu();
            }
        });

        // Set ActionBar stuff
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Hello NoteActivity!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(mOptionsMenuResourceId, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_save_note:
                saveNote();
                return true;
            case R.id.menu_item_edit_note:
                // TODO implement this
                mViewModel.setIsEditing(true);
                return true;
            case R.id.menu_item_favorite_note:
                // TODO implement this
                return true;
            case R.id.menu_item_delete_note:
                // TODO implement this
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Private Methods

    /**
     * Sends View data to the parent Activity.
     */
    private void saveNote() {
        // Get data from Views
        String title = mEtTitle.getText().toString();
        String content = mEtContent.getText().toString();

        // Check if title and content are both empty
        if (title.trim().isEmpty() && content.trim().isEmpty()) {
            // discard Note
            Toast.makeText(this, getString(R.string.toast_note_discarded), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // TODO handle updating an existing Note; the following code assumes we're creating a new one

        // TODO check if any data has actually changed from the one that's already stored in the database

        // TODO create a Note and insert/update it via the ViewModel
        long currentTime = Calendar.getInstance().getTimeInMillis();
        String category = ""; // TODO get category
        boolean isFavorited = false; // TODO get isFavorited
        Note note = new Note(title, content, currentTime, currentTime, category, isFavorited);

        mViewModel.insert(note);

        // Change isEditing state
        mViewModel.setIsEditing(false);

        // TODO delete this; just for debugging
        Toast.makeText(this, "Note saved.", Toast.LENGTH_SHORT).show();
    }
}
