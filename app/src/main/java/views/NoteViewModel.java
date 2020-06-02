package views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.NoteEntity;
import repository.AppRepository;

public class NoteViewModel extends AndroidViewModel
{
    private AppRepository repository;
    private LiveData<List<NoteEntity>> allNotes;


    public NoteViewModel(@NonNull Application application)
    {
        super(application);

    }

    /*public NoteViewModel(@NonNull Application application)
    {
        super(application);
        repository = new AppRepository(application);
        allNotes = repository.getAllNotes();
    }

    public void insert (NoteEntity note)
    {
        repository.insert(note);
    }

    public void update (NoteEntity note)
    {
        repository.update(note);
    }

    public void delete (NoteEntity note)
    {
        repository.delete(note);
    }

    public LiveData<List<NoteEntity>> getAllNotes()
    {
        return allNotes;
    }

    */
}
