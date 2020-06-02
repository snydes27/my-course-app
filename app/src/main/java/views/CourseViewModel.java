package views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Course;
import repository.AppRepository;

public class CourseViewModel extends AndroidViewModel
{

    public LiveData<List<Course>> mCourses;
    private AppRepository mRepository;

    public CourseViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mCourses = mRepository.mCourses;
    }
}

