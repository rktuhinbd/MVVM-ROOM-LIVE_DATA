package com.rktuhinbd.mvvm_room_livedata.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.rktuhinbd.mvvm_room_livedata.R;
import com.rktuhinbd.mvvm_room_livedata.Utility.Keys;


public class NoteInsertUpdateActivity extends AppCompatActivity {

    private EditText editTextTitle, editTextDescription;
    private NumberPicker numberPickerPriority;

    private String title, description;
    private int id, priority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        //Initiate Properties
        editTextTitle = findViewById(R.id.editText_title);
        editTextDescription = findViewById(R.id.editText_description);
        numberPickerPriority = findViewById(R.id.numberPicker_priority);

        numberPickerPriority.setMinValue(1);
        numberPickerPriority.setMaxValue(10);

        //Set Tool Bar title and Set data to view properties if exists
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Intent intent = getIntent();
        if (intent.hasExtra(Keys.ID_KEY)) {
            setTitle("Edit Note");
            id = intent.getIntExtra(Keys.ID_KEY, -1);
            title = intent.getStringExtra(Keys.TITLE_KEY);
            description = intent.getStringExtra(Keys.DESCRIPTION_KEY);
            priority = intent.getIntExtra(Keys.PRIORITY_KEY, 1);

            editTextTitle.setText(title);
            editTextDescription.setText(description);
            numberPickerPriority.setValue(priority);
        } else {
            setTitle("Add Note");
        }

    }

    private void saveNote() {
        title = editTextTitle.getText().toString().trim();
        description = editTextDescription.getText().toString().trim();
        priority = numberPickerPriority.getValue();

        if (title.isEmpty() || description.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Title and Description required", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(Keys.TITLE_KEY, title);
        data.putExtra(Keys.DESCRIPTION_KEY, description);
        data.putExtra(Keys.PRIORITY_KEY, priority);
        if (id != -1) {
            data.putExtra(Keys.ID_KEY, id);
        }

        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_note:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
