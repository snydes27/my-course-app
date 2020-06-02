package views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Assessment;
import entity.Course;
import entity.Term;
import repository.AppRepository;

public class MainViewModel extends AndroidViewModel {

    public LiveData<List<Term>> mTerms;
    public LiveData<List<Course>> mCourses;
    public LiveData<List<Assessment>> mAssessments;
    private AppRepository mRepository;

    public MainViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mTerms = mRepository.getAllTerms();
        mCourses = mRepository.getAllCourses();
        mAssessments = mRepository.getAllAssessments();

    }

    public LiveData<List<Term>> getAllTerms() {
        return mRepository.getAllTerms();
    }

    /*
    public void addSampleData() {
        mRepository.addSampleData();
    }

     */

    public void deleteAllData() {
        mRepository.deleteAllData();
    }
}
