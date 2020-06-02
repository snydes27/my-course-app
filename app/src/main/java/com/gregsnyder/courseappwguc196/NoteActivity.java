package com.gregsnyder.courseappwguc196;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import database.NotesAdapter;
import entity.NoteEntity;
import views.NoteViewModel;

public class NoteActivity extends AppCompatActivity
{
    public static final int ADD_NOTE_REQUEST = 1;
    private NoteViewModel noteViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_list_view);

        RecyclerView recyclerView = findViewById(R.id.note_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);

        final NotesAdapter adapter = new NotesAdapter();
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        setTitle("All Notes");

        /*
        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<NoteEntity>>()
        {
            @Override
            public void onChanged(@Nullable List<NoteEntity> notes)
            {
                adapter.setNotes(notes);
            }
        });

         */

        FloatingActionButton fab = findViewById(R.id.button_add_note);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NoteActivity.this, NoteEditActivity.class);
                startActivityForResult(intent, ADD_NOTE_REQUEST);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_NOTE_REQUEST && resultCode == RESULT_OK)
        {
            String noteTitle = data.getStringExtra(NoteEditActivity.EXTRA_NOTE_TITLE);
            String noteBody = data.getStringExtra(NoteEditActivity.EXTRA_NOTE_BODY);
            int courseID = data.getIntExtra(NoteEditActivity.EXTRA_COURSE_ID, 1);

            NoteEntity note = new NoteEntity(noteTitle, noteBody, courseID);
            NoteEntity.insert(note);

            Toast.makeText(this,"NoteEntity Saved", Toast.LENGTH_SHORT).show();
        } else
        {
            Toast.makeText(this, "NoteEntity Save Error", Toast.LENGTH_SHORT).show();
        }
    }

}
