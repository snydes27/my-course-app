package views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Assessment;
import repository.AppRepository;

public class AssessmentViewModel extends AndroidViewModel
{

    public LiveData<List<Assessment>> mAssessments;
    private AppRepository mRepository;

    public AssessmentViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mAssessments = mRepository.mAssessments;
    }
}