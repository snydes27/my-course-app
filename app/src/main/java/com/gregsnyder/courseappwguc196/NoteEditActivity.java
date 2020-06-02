package com.gregsnyder.courseappwguc196;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class NoteEditActivity extends AppCompatActivity
{

    public static final String EXTRA_NOTE_TITLE = "com.gregsnyder.courseappwguc196.EXTRA_NOTE_TITLE";
    public static final String EXTRA_NOTE_BODY = "com.gregsnyder.courseappwguc196.EXTRA_NOTE_BODY";
    public static final String EXTRA_COURSE_ID = "com.gregsnyder.courseappwguc196.EXTRA_COURSE_ID";

    private EditText noteTitleEditText;
    private EditText noteBodyEditText;
    private int noteCourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_add);

        noteTitleEditText = findViewById(R.id.add_note_title);
        noteBodyEditText = findViewById(R.id.add_note_body);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("Add NoteEntity");
    }

    private void saveAssessment()
    {
        String title = noteTitleEditText.getText().toString();
        String body = noteBodyEditText.getText().toString();
        int courseID = noteCourseID;

        if (title.trim().isEmpty() || body.trim().isEmpty())
        {
            Toast.makeText(this, "Please insert a Title and Date", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent data = new Intent();
        data.putExtra(EXTRA_NOTE_TITLE, title);
        data.putExtra(EXTRA_NOTE_BODY, body);
        data.putExtra(EXTRA_COURSE_ID, courseID);

        setResult(RESULT_OK, data);
        finish();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.save_item:
                saveAssessment();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
