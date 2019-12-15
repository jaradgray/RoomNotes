package com.jgendeavors.roomnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.jgendeavors.roomnotes.viewmodels.NoteActivityViewModel;

public class NoteActivity extends AppCompatActivity {
    // Intent Extras
    public static final String EXTRA_TITLE = "com.jgendeavors.roomnotes.EXTRA_TITLE";
    public static final String EXTRA_CONTENT = "com.jgendeavors.roomnotes.EXTRA_CONTENT";

    // References to Views
    private EditText mEtTitle;
    private EditText mEtContent;

    private NoteActivityViewModel mViewModel;
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
            public void onChanged(Boolean aBoolean) {
                // TODO change options menu resource
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int menuId = mIsEditing ? R.menu.activity_note_editing_menu : R.menu.activity_note_normal_menu;
        getMenuInflater().inflate(menuId, menu);
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

        // TODO check to determine if we should actually update this Note in the database

        // TODO this Activity should have its own ViewModel so we can call insert()/update() from here instead of passing stuff back to MainActivity

        // TODO create a Note and insert/update it via the ViewModel
    }
}
