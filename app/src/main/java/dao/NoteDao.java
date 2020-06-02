package dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import entity.NoteEntity;

@Dao
public interface NoteDao
{

    @Insert
    void insert(NoteEntity term);

    @Update
    void update(NoteEntity term);

    @Delete
    void delete(NoteEntity term);

    @Query("SELECT * FROM NoteEntity /* WHERE course_ID_col = :courseID*/  ORDER BY note_title_col DESC")
    LiveData<List<NoteEntity>> getAllNotes();


}