package entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity (tableName = "note_table")
public class NoteEntity
{
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "note_ID_col")
    private int noteID;

    @ColumnInfo(name = "note_title_col")
    private String noteTitle;

    //@TypeConverters(NoteBody.class)
    @ColumnInfo(name = "note_body_col")
    private String noteBody;

    @ColumnInfo(name = "course_ID_col")
    private int courseID;

    //Constructor

    public NoteEntity(String noteTitle, String noteBody, int courseID)
    {
        this.noteTitle = noteTitle;
        this.noteBody = noteBody;
        this.courseID = courseID;
    }

    @Override
    public String toString()
    {
        return "NoteEntity{" +
                "noteID=" + noteID +
                ", noteTitle='" + noteTitle + '\'' +
                ", noteBody='" + noteBody + '\'' +
                ", courseID=" + courseID +
                '}';
    }

    public static void insert(NoteEntity note) {
    }

    //Setter

    public void setNoteID(int noteID)
    {
        this.noteID = noteID;
    }

    //Getters

    public int getNoteID()
    {
        return noteID;
    }

    public String getNoteTitle()
    {
        return noteTitle;
    }

    public String getNoteBody()
    {
        return noteBody;
    }

    public int getCourseID()
    {
        return courseID;
    }
}
