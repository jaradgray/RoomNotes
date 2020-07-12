package com.jgendeavors.roomnotes;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jgendeavors.roomnotes.adapters.NoteAdapter;
import com.jgendeavors.roomnotes.entities.Note;
import com.jgendeavors.roomnotes.viewmodels.NoteActivityViewModel;
import com.jgendeavors.roomnotes.viewmodels.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // Instance Variables
    private NoteViewModel mNoteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set toolbar as support action bar
        final Toolbar toolbar = findViewById(R.id.activity_main_toolbar);
        setSupportActionBar(toolbar);

        // Connect toolbar to NavController
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupWithNavController(toolbar, navController);

        // Request a ViewModel from the Android system
        NoteActivityViewModel viewModel = ViewModelProviders.of(this).get(NoteActivityViewModel.class);

        // Observe viewModel's LiveData
        viewModel.getIsEditing().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isEditing) {
                // Update actionbar UI based on isEditing:
                //  hide up/back arrow if isEditing
                //  show it if !isEditing
                getSupportActionBar().setDisplayHomeAsUpEnabled(!isEditing);
            }
        });
    }
}
