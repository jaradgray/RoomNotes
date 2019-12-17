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

        // Create the ViewModel that will drive this Activity's UI
        mViewModel = ViewModelProviders.of(this).get(NoteActivityViewModel.class);

        // set ViewModel state based on if we're editing a NEW Note, or reading an EXISTING Note
        if (getIntent().hasExtra(EXTRA_ID)) {
            int noteId = getIntent().getIntExtra(EXTRA_ID, EXTRA_VALUE_NO_ID);
            Toast.makeText(this, "noteId: " + noteId, Toast.LENGTH_SHORT).show();
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
                // Update UI in response to change in isEditing state
                if (isEditing) {
                    mOptionsMenuResourceId = R.menu.activity_note_editing_menu;
                    showSoftKeyboard(mEtContent);
                } else {
                    mOptionsMenuResourceId = R.menu.activity_note_normal_menu;
                    hideSoftKeyboard(mEtContent);
                }
                invalidateOptionsMenu();
            }
        });

        // observe changes to the ViewModel's Note
        mViewModel.getNote().observe(this, new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note == null) {
                    // TODO set View data to indicate no Note
                } else {
                    // set View data to match Note data
                    mEtTitle.setText(note.getTitle());
                    mEtContent.setText(note.getContent());
                    String dateLastModifiedText = getString(R.string.activity_note_date_modified_format, getTimeAsString(note.getDateModified()));
                    mTvDate.setText(dateLastModifiedText);
                }
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
    private void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    /**
     * Hide the soft keyboard using the windowToken of the given view.
     *
     * @param view
     */
    private void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    /**
     * TODO document this method
     *
     * @param millis
     * @return
     */
    private String getTimeAsString(long millis) {
        final long millisPerDay = 1000 * 60 * 60 * 24;
        Calendar calendar = Calendar.getInstance();
        long currentTime = calendar.getTimeInMillis();
        calendar.setTimeInMillis(millis);

        String result = "";
        if (currentTime - millis < millisPerDay) {
            // millis represents a time within the last 24 hours, return a time like "8:43 PM"
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String ampm = calendar.getDisplayName(Calendar.AM_PM, Calendar.LONG, Locale.getDefault());

            result = getString(R.string.activity_note_hour_format, hour, minute, ampm);
        } else {
            // millis represents a time longer than 24 hours ago, return a date like "December 13, 2019"
            String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int year = calendar.get(Calendar.YEAR);

            result = getString(R.string.activity_note_day_format, month, day, year);
        }
        return result;
    }
}
