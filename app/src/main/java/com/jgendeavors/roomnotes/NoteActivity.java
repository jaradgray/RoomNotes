package com.jgendeavors.roomnotes;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.util.Util;
import com.jgendeavors.roomnotes.viewmodels.NoteActivityViewModel;

import java.util.Calendar;
import java.util.Locale;

public class NoteActivity extends AppCompatActivity {
    // Intent Extras
    public static final String EXTRA_ID = "com.jgendeavors.roomnotes.EXTRA_ID";
    public static final int EXTRA_VALUE_NO_ID = -1;


    // References to Views
    private EditText mEtTitle;
    private EditText mEtContent;
    private TextView mTvCharacterCount;
    private TextView mTvDate;

    private NoteActivityViewModel mViewModel;
    private int mOptionsMenuResourceId;


    // Overridden Methods

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        // Get references to Views
        mEtTitle = findViewById(R.id.activity_note_et_title);
        mEtContent = findViewById(R.id.activity_note_et_content);
        mTvCharacterCount = findViewById(R.id.activity_note_tv_charactercount);
        mTvDate = findViewById(R.id.activity_note_tv_date);

        // update mTvCharacterCount's text when mEtContent's text changes
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mTvCharacterCount.setText(getString(R.string.activity_note_character_count_format, editable.length()));
            }
        });

        // The OnFocusChangeListener that will set ViewModel's isEditing flag
        // when the Views it's applied to receive focus
        View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                // Check if view has focus and ViewModel isn't already in isEditing state
                if (hasFocus && mViewModel != null && !mViewModel.getIsEditing().getValue()) {
                    mViewModel.setIsEditing(true);
                }
            }
        };
        mEtTitle.setOnFocusChangeListener(focusChangeListener);
        mEtContent.setOnFocusChangeListener(focusChangeListener);

        // Set ActionBar stuff
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("");

        // Create the ViewModel that will drive this Activity's UI
        mViewModel = ViewModelProviders.of(this).get(NoteActivityViewModel.class);

        // set ViewModel state based on if we're editing a NEW Note, or reading an EXISTING Note
        if (getIntent().hasExtra(EXTRA_ID)) {
            int noteId = getIntent().getIntExtra(EXTRA_ID, EXTRA_VALUE_NO_ID);
            if (EXTRA_VALUE_NO_ID == noteId) {
                // editing a new Note
                mViewModel.setIsEditing(true);
            } else {
                // reading an existing Note
                mViewModel.setIsEditing(false);
                mViewModel.setNoteById(noteId);
            }
        } else {
            Toast.makeText(this, getString(R.string.toast_error_note_id_not_set), Toast.LENGTH_SHORT).show();
        }

        // observe changes to the isEditing state
        mViewModel.getIsEditing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEditing) {
                // showSoftKeyboard(View) will focus this View unless it already has focus.
                // We'll set it to mEtContent unless mEtTitle has focus
                View focusedView = (mEtTitle.hasFocus()) ? mEtTitle : mEtContent;
                // Update UI in response to change in isEditing state
                if (isEditing) {
                    actionBar.setDisplayHomeAsUpEnabled(false);
                    mOptionsMenuResourceId = R.menu.activity_note_editing_menu;
                    showSoftKeyboard(focusedView);
                } else {
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    mOptionsMenuResourceId = R.menu.activity_note_normal_menu;
                    hideSoftKeyboard(focusedView);
                }
                invalidateOptionsMenu();
            }
        });

        // observe changes to the ViewModel's Note
        mViewModel.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note == null) {
                    // set View data to indicate no Note
                    mTvCharacterCount.setText(getString(R.string.activity_note_character_count_format, 0));
                    mTvDate.setVisibility(View.GONE);
                } else {
                    // set View data to match Note data
                    mEtTitle.setText(note.getTitle());
                    mEtContent.setText(note.getContent());
                    String dateLastModifiedText = getString(R.string.activity_note_date_modified_format,
                            Util.getTimeAsString(NoteActivity.this, note.getDateModified(), Calendar.LONG));
                    mTvDate.setVisibility(View.VISIBLE);
                    mTvDate.setText(dateLastModifiedText);
                }
            }
        });
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
                mViewModel.setIsEditing(true);
                return true;
            case R.id.menu_item_favorite_note:
                // TODO implement this
                return true;
            case R.id.menu_item_delete_note:
                deleteNote();
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Private Methods

    /**
     *
     */
    private void saveNote() {
        // Get data from Views
        String title = mEtTitle.getText().toString().trim();
        String content = mEtContent.getText().toString().trim();

        // TODO keep as little logic as possible in the Activity, let the ViewModel handle as much as possible

        // Check if title and content are both empty
        if (title.isEmpty() && content.isEmpty()) {
            // discard Note
            // delete the Note if we're dealing with an existing one
            if (mViewModel.getNote().getValue() != null) {
                deleteNote();
            }
            Toast.makeText(this, getString(R.string.toast_note_discarded), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // save the Note we're dealing with via the ViewModel
        String category = ""; // TODO get category
        boolean isFavorited = false; // TODO get isFavorited

        mViewModel.saveNote(title, content, category, isFavorited);

        // Change isEditing state
        mViewModel.setIsEditing(false);
    }

    /**
     * Delete the Note we're dealing with from the database, via the ViewModel.
     */
    private void deleteNote() {
        mViewModel.deleteNote();
    }

    /**
     * Show the soft keyboard if the given View is granted focus.
     *
     * @param view
     */
    private void showSoftKeyboard(final View view) {
        if (!view.hasFocus() && view.requestFocus()) {
            final InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            // Show keyboard by posting a Runnable as per: https://stackoverflow.com/a/27540921
            // to sidestep keyboard not showing when Activity is launched to edit a new Note
            view.postDelayed(new Runnable() {
                @Override
                public void run() {
                    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        }
    }

    /**
     * Hide the soft keyboard using the windowToken of the given view.
     *
     * @param view
     */
    private void hideSoftKeyboard(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // idk why this works in all cases but the commented-out code doesn't...
    }
}
