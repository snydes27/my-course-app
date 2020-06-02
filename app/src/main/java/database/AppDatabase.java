package database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import dao.AssessmentDao;
import dao.CourseDao;
import dao.TermDao;
import entity.Assessment;
import entity.Course;
import entity.Term;

@Database(entities = {Term.class, Course.class, Assessment.class,}, version = 9)
@TypeConverters({DateConverter.class, CourseStatusConverter.class, AssessmentTypeConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "AppDatabase.db";
    private static volatile AppDatabase instance;
    private static final Object LOCK = new Object();

    public abstract TermDao termDao();
    public abstract CourseDao courseDao();
    public abstract AssessmentDao assessmentDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
                }
            }
        }

        return instance;
    }
}
