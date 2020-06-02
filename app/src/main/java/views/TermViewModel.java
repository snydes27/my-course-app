package views;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

import entity.Term;
import repository.AppRepository;

public class TermViewModel extends AndroidViewModel
{

    public LiveData<List<Term>> mTerms;
    private AppRepository mRepository;

    public TermViewModel(@NonNull Application application) {
        super(application);

        mRepository = AppRepository.getInstance(application.getApplicationContext());
        mTerms = mRepository.mTerms;
    }
}