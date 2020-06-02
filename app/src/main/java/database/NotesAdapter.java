package database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gregsnyder.courseappwguc196.R;

import java.util.ArrayList;
import java.util.List;

import entity.NoteEntity;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteHolder>
{

    private List<NoteEntity> notes = new ArrayList<>();

    class NoteHolder extends RecyclerView.ViewHolder
    {
        private TextView textViewNoteTitle;
        private TextView textViewNoteBody;

        public NoteHolder(@NonNull View itemView)
        {
            super(itemView);
            textViewNoteTitle = itemView.findViewById(R.id.list_note_title);
            textViewNoteBody = itemView.findViewById(R.id.list_note_body);

        }
    }


    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_notes_detail_view, parent, false);
        return new NoteHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position)
    {
        NoteEntity currentNote = notes.get(position);
        holder.textViewNoteTitle.setText(currentNote.getNoteTitle());
        holder.textViewNoteBody.setText(String.valueOf(currentNote.getNoteBody()));

    }

    @Override
    public int getItemCount()
    {
        return notes.size();
    }

    public void setNotes(List<NoteEntity> notes)
    {
        this.notes = notes;
        notifyDataSetChanged();
    }
}

