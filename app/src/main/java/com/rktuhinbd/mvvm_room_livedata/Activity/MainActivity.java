package com.rktuhinbd.mvvm_room_livedata.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.rktuhinbd.mvvm_room_livedata.Adapter.NoteAdapter;
import com.rktuhinbd.mvvm_room_livedata.R;
import com.rktuhinbd.mvvm_room_livedata.RoomDatabase.Note;
import com.rktuhinbd.mvvm_room_livedata.Utility.Keys;
import com.rktuhinbd.mvvm_room_livedata.ViewModel.NoteViewModel;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_NOTE_REQUEST = 1;
    public static final int EDIT_NOTE_REQUEST = 2;

    private String title, description;
    private int id, priority;

    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //RecyclerView to show notes
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NoteAdapter noteAdapter = new NoteAdapter();
        recyclerView.setAdapter(noteAdapter);

        //Floating action button to add note
        FloatingActionButton fabAddNote = findViewById(R.id.fab);
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), NoteInsertUpdateActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });

        //Observe if note data is changing and set them into recyclerView adapter
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                noteAdapter.submitList(notes);
            }
        });

        //Implement recycler item click listener
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent intent = new Intent(getApplicationContext(), NoteInsertUpdateActivity.class);
                intent.putExtra(Keys.ID_KEY, note.getId());
                intent.putExtra(Keys.TITLE_KEY, note.getTitle());
                intent.putExtra(Keys.DESCRIPTION_KEY, note.getDescription());
                intent.putExtra(Keys.PRIORITY_KEY, note.getPriority());
                startActivityForResult(intent, EDIT_NOTE_REQUEST);
            }
        });

        //Implement swipe to delete item
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(noteAdapter.getNotePosition(viewHolder.getAdapterPosition()));
            }
        }).attachToRecyclerView(recyclerView);
    }

    //Add a note
    private void addNote() {
        Note note = new Note(title, description, priority);
        noteViewModel.insert(note);
        Toast.makeText(getApplicationContext(), "Note saved", Toast.LENGTH_SHORT).show();
    }

    //Delete all notes functionality
    private void deleteAllNotes() {
        noteViewModel.deleteAllNotes();
        Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
    }

    //Menu bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.delete_all_notes_menu, menu);
        return true;
    }

    //On menu item click action
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                deleteAllNotes();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Add note functionality
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK) {
            title = data.getStringExtra(Keys.TITLE_KEY);
            description = data.getStringExtra(Keys.DESCRIPTION_KEY);
            priority = data.getIntExtra(Keys.PRIORITY_KEY, 1);

            addNote();
        } else if (requestCode == EDIT_NOTE_REQUEST && resultCode == RESULT_OK) {
            id = data.getIntExtra(Keys.ID_KEY, -1);
            title = data.getStringExtra(Keys.TITLE_KEY);
            description = data.getStringExtra(Keys.DESCRIPTION_KEY);
            priority = data.getIntExtra(Keys.PRIORITY_KEY, 1);

            if (id == -1) {
                Toast.makeText(this, "Note update failure!", Toast.LENGTH_SHORT).show();
            } else {
                Note note = new Note(title, description, priority);
                note.setId(id);
                noteViewModel.update(note);
                Toast.makeText(this, "Note updated successfully", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
