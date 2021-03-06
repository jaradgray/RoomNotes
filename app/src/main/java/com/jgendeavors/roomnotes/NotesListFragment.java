package com.jgendeavors.roomnotes;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jgendeavors.roomnotes.adapters.NoteAdapter;
import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.viewmodels.NoteViewModel;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class NotesListFragment extends Fragment {
    // Instance variables
    NoteAdapter mAdapter;

    // Lifecycle method overrides

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notes_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Indicate we have an options menu
        setHasOptionsMenu(true);

        // put any usages of findViewById() here

        // Initialize RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.fragment_notes_list_recyclerview);
        // Every RecyclerView needs a LayoutManager. Our RecyclerView will display items
        // in a vertical list, so we need a LinearLayoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.setHasFixedSize(true); // improves performance when we know the size of our RecyclerView in the layout won't change
        // Every RecyclerView needs a RecyclerView.Adapter. We'll need a reference to it, too.
        mAdapter = new NoteAdapter();
        recyclerView.setAdapter(mAdapter);

        // Get the Activity's NavController
        final NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);

        // Handle clicks on RecyclerView items by implementing NoteAdapter.OnItemClickListener interface
        mAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(Note note) {
                // Navigate to NoteDetailFragment
                // build the bundle that will hold the clicked Note's id
                Bundle args = new Bundle();
                args.putInt(NoteDetailFragment.ARG_KEY_ID, note.getId());
                // navigate
                navController.navigate(R.id.action_notesListFragment_to_noteDetailFragment, args);
            }
        });

        // Set up FAB
        FloatingActionButton fab = view.findViewById(R.id.fragment_notes_list_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to NoteDetailFragment
                // build the bundle that will hold the clicked Note's id
                Bundle args = new Bundle();
                args.putInt(NoteDetailFragment.ARG_KEY_ID, NoteDetailFragment.ARG_VALUE_NO_ID);
                // navigate
                navController.navigate(R.id.action_notesListFragment_to_noteDetailFragment, args);
            }
        });

        // Request a NoteViewModel from the Android system
        NoteViewModel viewModel = ViewModelProviders.of(requireActivity()).get(NoteViewModel.class);

        // observe the ViewModel's LiveData
        viewModel.getAllNotes().observe(getViewLifecycleOwner(), new Observer<List<Note>>() {
            /**
             * Called each time the data in the LiveData object we're observing changes.
             * @param notes
             */
            @Override
            public void onChanged(List<Note> notes) {
                // update RecyclerView UI
                mAdapter.setNotes(notes);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Inflate our options menu
        inflater.inflate(R.menu.fragment_notes_list_options_menu, menu);
        // Get a reference to our menu's SearchView, so we can add a listener to filter the Notes list
        MenuItem searchItem = menu.findItem(R.id.menu_item_search_notes);
        SearchView searchView = (SearchView)searchItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE); // change to more appropriate keyboard action button
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // we'll filter on text changed, so nothing to do here
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText); // filter RecyclerView based on newText
                return false;
            }
        });
    }
}
