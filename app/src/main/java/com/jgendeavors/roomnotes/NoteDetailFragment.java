package com.jgendeavors.roomnotes;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.util.Util;
import com.jgendeavors.roomnotes.viewmodels.NoteViewModel;

import java.util.Calendar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import io.noties.markwon.Markwon;
import io.noties.markwon.editor.MarkwonEditor;
import io.noties.markwon.editor.MarkwonEditorTextWatcher;

public class NoteDetailFragment extends Fragment {
    // Navigation args
    public static final String ARG_KEY_ID = "com.jgendeavors.roomnotes.ARG_KEY_ID";
    public static final int ARG_VALUE_NO_ID = -1;

    // Instance variables
    private TextView mTvRenderedTitle;
    private EditText mEtTitle;
    private TextView mTvRenderedContent;
    private EditText mEtContent;
    private TextView mTvCharacterCount;
    private TextView mTvDate;

    private NoteViewModel mViewModel;
    private int mOptionsMenuResourceId;


    // Overridden methods

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Indicate we have an options menu
        setHasOptionsMenu(true);

        // put any usages of findViewById() here

        // Get references to widgets
        mTvRenderedTitle = view.findViewById(R.id.fragment_note_detail_tv_rendered_title);
        mEtTitle = view.findViewById(R.id.fragment_note_detail_et_title);
        mTvRenderedContent = view.findViewById(R.id.fragment_note_detail_tv_rendered_content);
        mEtContent = view.findViewById(R.id.fragment_note_detail_et_content);
        mTvCharacterCount = view.findViewById(R.id.fragment_note_detail_tv_charactercount);
        mTvDate = view.findViewById(R.id.fragment_note_detail_tv_date);

        // Make EditTexts be MarkwonEditors
        final Markwon markwon = Markwon.create(requireActivity());
        final MarkwonEditor editor = MarkwonEditor.create(markwon);
        mEtTitle.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));
        mEtContent.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(editor));

        // update mTvCharacterCount's text when mEtContent's text changes
        mEtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                mTvCharacterCount.setText(getString(R.string.fragment_note_detail_character_count_format, editable.length()));
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

        // Handle clicks on the root Layout
        ViewGroup rootLayout = view.findViewById(R.id.fragment_note_detail_root_layout);
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // set ViewModel's isEditing flag, focus mEtContent, and move cursor to end of content
                if (mViewModel != null && !mViewModel.getIsEditing().getValue()) {
                    mViewModel.setIsEditing(true);
                }
                mEtContent.requestFocus();
                mEtContent.setSelection(mEtContent.length());
            }
        });

        // TODO Set ActionBar stuff ???
//        final ActionBar actionBar = getSupportActionBar();
//        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_left);
//        setTitle("");

        // Request a ViewModel from the Android system
        mViewModel = ViewModelProviders.of(requireActivity()).get(NoteViewModel.class);

        // set ViewModel state based on if we're editing a NEW Note, or reading an EXISTING Note
        int noteId = getArguments().getInt(ARG_KEY_ID, ARG_VALUE_NO_ID);
        if (ARG_VALUE_NO_ID == noteId) {
            // editing a new Note
            mViewModel.setIsEditing(true);
        } else {
            // reading an existing Note
            mViewModel.setIsEditing(false);
        }
        mViewModel.setNoteById(noteId);

        // observe changes to the isEditing state
        mViewModel.getIsEditing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEditing) {
                // showSoftKeyboard(View) will focus this View unless it already has focus.
                // We'll set it to mEtContent unless mEtTitle has focus
                View focusedView = (mEtTitle.hasFocus()) ? mEtTitle : mEtContent;

                // Update UI in response to change in isEditing state
                if (isEditing) {
                    ((AppCompatActivity)requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                    mOptionsMenuResourceId = R.menu.fragment_note_detail_editing_menu;
                    showSoftKeyboard(focusedView);
                    mEtTitle.setHint(R.string.fragment_note_detail_title_hint_editing);
                } else {
                    ((AppCompatActivity)requireActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    mOptionsMenuResourceId = R.menu.fragment_note_detail_normal_menu;
                    hideSoftKeyboard(focusedView);
                    mEtTitle.setHint(R.string.fragment_note_detail_title_hint_normal);
                }
                requireActivity().invalidateOptionsMenu();
            }
        });

        // observe changes to the ViewModel's Note
        mViewModel.getNote().observe(getViewLifecycleOwner(), new Observer<Note>() {
            @Override
            public void onChanged(Note note) {
                if (note == null) {
                    // set ViewModel's RenderMode to Plaintext
                    mViewModel.setRenderMode(NoteViewModel.RenderMode.Plaintext);
                    // set View data to indicate no Note
                    mTvCharacterCount.setText(getString(R.string.fragment_note_detail_character_count_format, 0));
                    mTvDate.setVisibility(View.GONE);
                } else {
                    // Set View data to match Note data
                    // EditTexts get plaintext
                    mEtTitle.setText(note.getTitle());
                    mEtContent.setText(note.getContent());
                    // TextViews get rendered markdown
                    markwon.setMarkdown(mTvRenderedTitle, note.getTitle());
                    markwon.setMarkdown(mTvRenderedContent, note.getContent());
                    // date
                    String dateLastModifiedText = getString(R.string.fragment_note_detail_date_modified_format,
                            Util.getTimeAsString(requireActivity(), note.getDateModified(), Calendar.LONG));
                    mTvDate.setVisibility(View.VISIBLE);
                    mTvDate.setText(dateLastModifiedText);
                }
            }
        });

        // observe ViewModel's RenderMode
        mViewModel.getRenderMode().observe(getViewLifecycleOwner(), new Observer<NoteViewModel.RenderMode>() {
            @Override
            public void onChanged(NoteViewModel.RenderMode renderMode) {
                // Show plaintext or markdown, and update actionbar icon
                switch (renderMode) {
                    case Plaintext:
                        showPlaintext();
                        break;
                    case Markdown:
                        showRenderedMarkdown();
                        break;
                }
                requireActivity().invalidateOptionsMenu();
            }
        });

        // Make the ActionBar display no title when we're viewing this Fragment.
        //  This seems hacky, but I couldn't find a simple solution to hide the title for specific Navigation destinations
        ((AppCompatActivity)requireActivity()).getSupportActionBar().setTitle(R.string.fragment_note_detail_label);
    }


    // Overrides for options menu

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate our options menu
        inflater.inflate(mOptionsMenuResourceId, menu);
    }

    /**
     * Called after the options menu has been inflated.
     * @param menu
     */
    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // TODO we could probably save references to these MenuItems as instance variables and set
        //  their icons elsewhere, instead of having to override this method and call invalidateOptionsMenu()
        //  every time we want to change an icon
        // Set favorite icon based on Note.isFavorited
        MenuItem favItem = menu.findItem(R.id.menu_item_favorite_note);
        Note note = mViewModel.getNote().getValue();
        if (favItem != null && note != null && note.getIsFavorited()) {
            favItem.setIcon(R.drawable.ic_favorite_filled);
        }
        // Set "toggle view" icon based on ViewModel's RenderMode
        MenuItem viewItem = menu.findItem(R.id.menu_item_toggle_view);
        NoteViewModel.RenderMode renderMode = mViewModel.getRenderMode().getValue();
        if (viewItem != null && renderMode != null) {
            // get the icon's resId based on renderMode
            int icViewResId = (renderMode.equals(NoteViewModel.RenderMode.Plaintext)) ? R.drawable.ic_view_markdown : R.drawable.ic_view_plaintext;
            viewItem.setIcon(icViewResId);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle options items
        switch (item.getItemId()) {
            case R.id.menu_item_save_note:
                saveNote();
                return true;
            case R.id.menu_item_toggle_view:
                // toggle ViewModel's render mode
                mViewModel.toggleRenderMode();
                // note: we don't have to call invalidateOptionsMenu() because it's called by the Observer
                return true;
            case R.id.menu_item_favorite_note:
                mViewModel.toggleFavorite();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.menu_item_delete_note:
                deleteNote();
                // Navigate up/back
                Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    // Private methods

    /**
     *
     */
    private void saveNote() {
        // Get data from Views
        String title = mEtTitle.getText().toString().trim();
        String content = mEtContent.getText().toString().trim();

        // TODO keep as little logic as possible here; let the ViewModel handle as much as possible

        // Check if title and content are both empty
        if (title.isEmpty() && content.isEmpty()) {
            // discard Note
            // delete the Note if we're dealing with an existing one
            if (mViewModel.getNote().getValue() != null) {
                deleteNote();
            }
            Toast.makeText(requireActivity(), getString(R.string.toast_note_discarded), Toast.LENGTH_SHORT).show();

            // Navigate up/back
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment).popBackStack();
            return;
        }

        // save the Note we're dealing with via the ViewModel
        String category = ""; // TODO get category
        boolean isFavorited = mViewModel.getNote().getValue() != null && mViewModel.getNote().getValue().getIsFavorited();

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
     * Shows the soft keyboard if the given View is granted focus.
     *
     * @param view
     */
    private void showSoftKeyboard(final View view) {
        if (!view.hasFocus() && view.requestFocus()) {
            final InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
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
     * Hides the soft keyboard using the windowToken of the given view.
     *
     * @param view
     */
    private void hideSoftKeyboard(View view) {
        view.clearFocus();
        InputMethodManager imm = (InputMethodManager)requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); // idk why this works in all cases but the commented-out code doesn't...
    }

    /**
     * Hides rendered markdown TextViews and shows plaintext EditTexts.
     */
    private void showPlaintext() {
        mTvRenderedTitle.setVisibility(View.GONE);
        mTvRenderedContent.setVisibility(View.GONE);
        mEtTitle.setVisibility(View.VISIBLE);
        mEtContent.setVisibility(View.VISIBLE);
    }

    /**
     * Hides plaintext EditTexts and shows rendered markdown TextViews.
     */
    private void showRenderedMarkdown() {
        mEtTitle.setVisibility(View.GONE);
        mEtContent.setVisibility(View.GONE);
        mTvRenderedTitle.setVisibility(View.VISIBLE);
        mTvRenderedContent.setVisibility(View.VISIBLE);
    }
}
