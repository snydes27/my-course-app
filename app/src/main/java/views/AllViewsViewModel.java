package views;

import android.app.Application;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import entity.Assessment;
import entity.AssessmentType;
import entity.Course;
import entity.CourseStatus;
import entity.Term;
import repository.AppRepository;

public class AllViewsViewModel extends AndroidViewModel {
    public MutableLiveData<Term> mLiveTerm = new MutableLiveData<>();
    public MutableLiveData<Course> mLiveCourse = new MutableLiveData<>();
    public MutableLiveData<Assessment> mLiveAssessment = new MutableLiveData<>();
    public LiveData<List<Term>> mTerms;
    public LiveData<List<Course>> mCourses;
    public LiveData<List<Assessment>> mAssessments;
    private AppRepository mRepository;
    private Executor executor = Executors.newSingleThreadExecutor();

    public AllViewsViewModel(@NonNull Application application) {
        super(application);
        mRepository = AppRepository.getInstance(getApplication());
        mTerms = mRepository.mTerms;
        mCourses = mRepository.mCourses;
        mAssessments = mRepository.mAssessments;
    }

    public void loadTermData(final int termId) {
        executor.execute(() -> {
            Term term = mRepository.getTermById(termId);
            mLiveTerm.postValue(term);
        });
    }

    public void loadCourseData(final int courseId) {
        executor.execute(() -> {
            Course course = mRepository.getCourseById(courseId);
            mLiveCourse.postValue(course);
        });
    }

    public void loadAssessmentData(final int assessmentId) {
        executor.execute(() -> {
            Assessment assessment = mRepository.getAssessmentById(assessmentId);
            mLiveAssessment.postValue(assessment);
        });
    }


    public void saveTerm(String termTitle, Date startDate, Date endDate) {
        Term term = mLiveTerm.getValue();

        if (term == null) {
            if (TextUtils.isEmpty(termTitle.trim())) {
                return;
            }
            term = new Term(termTitle.trim(), startDate, endDate);
        } else {
            term.setTitle(termTitle.trim());
            term.setStartDate(startDate);
            term.setEndDate(endDate);
        }
        mRepository.insertTerm(term);
    }

    public void saveCourse(String courseTitle, Date startDate, Date endDate, CourseStatus courseStatus, int termId, String note, String mentor) {
        Course course = mLiveCourse.getValue();

        if(course == null) {
            if (TextUtils.isEmpty(courseTitle.trim())) {
                return;
            }
            course = new Course(courseTitle.trim(), startDate, endDate, courseStatus, termId, note, mentor);
        } else {
            course.setTitle(courseTitle.trim());
            course.setStartDate(startDate);
            course.setAnticipatedEndDate(endDate);
            course.setCourseStatus(courseStatus);
            course.setTermId(termId);
            course.setNote(note);
            course.setMentor(mentor);
        }
        mRepository.insertCourse(course);
    }

    public void overwriteCourse(Course course, int termId) {
        course.setTermId(termId);
        mRepository.insertCourse(course);
    }

    public void overwriteAssessment(Assessment assessment, int courseId) {
        assessment.setCourseId(courseId);
        mRepository.insertAssessment(assessment);
    }


    public void saveAssessment(String assessmentTitle, Date date, AssessmentType assessmentType, int courseId) {
        Assessment assessment = mLiveAssessment.getValue();

        if(assessment == null) {
            if(TextUtils.isEmpty(assessmentTitle.trim())) {
                return;
            }
            assessment = new Assessment(assessmentTitle.trim(), date, assessmentType, courseId);
        } else {
            assessment.setTitle(assessmentTitle.trim());
            assessment.setDate(date);
            assessment.setAssessmentType(assessmentType);
        }
        mRepository.insertAssessment(assessment);
    }



    public void deleteTerm()
    {
        mRepository.deleteTerm(mLiveTerm.getValue());
    }

    public void deleteCourse()
    {
        mRepository.deleteCourse(mLiveCourse.getValue());
    }

    public void deleteAssessment()
    {
        mRepository.deleteAssessment(mLiveAssessment.getValue());
    }


    public LiveData<List<Course>> getCoursesInTerm(int termId)
    {
        return (mRepository.getCoursesByTerm(termId));
    }

    public LiveData<List<Assessment>> getAssessmentsInCourse(int courseId)
    {
        return (mRepository.getAssessmentsByCourse(courseId));
    }



    public LiveData<List<Course>> getUnassignedCourses()
    {
        return (mRepository.getCoursesByTerm(-1));
    }

    public LiveData<List<Assessment>> getUnassignedAssessments()
    {
        return (mRepository.getAssessmentsByCourse(-1));
    }



    public Term getTermById(int termId)
    {
        return mRepository.getTermById(termId);
    }
}
