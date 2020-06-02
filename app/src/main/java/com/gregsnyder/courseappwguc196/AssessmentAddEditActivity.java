package com.gregsnyder.courseappwguc196;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.AssessmentType;
import utilities.DateTextFormatting;
import views.AllViewsViewModel;

import static utilities.Constants.ASSESSMENT_ID_KEY;
import static utilities.Constants.COURSE_ID_KEY;
import static utilities.Constants.EDITING_KEY;
import static utilities.Constants.TERM_ID_KEY;

public class AssessmentAddEditActivity extends AppCompatActivity {
    @BindView(R.id.ass_edit_title)
    EditText tvAssessmentTitle;

    @BindView(R.id.ass_edit_date)
    EditText tvAssessmentDate;

    @BindView(R.id.ass_edit_type_dropdown)
    Spinner spAssessmentType;

    @BindView(R.id.startDateInMills)
    TextView startDateMills;

    private AllViewsViewModel mViewModel;
    private boolean mNewAssessment, mEditing;
    private ArrayAdapter<AssessmentType> assessmentTypeAdapter;
    private int courseId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_assessment_add_edit);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_save);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }

        initViewModel();
        addSpinnerItems();
    }

    private void addSpinnerItems() {
        assessmentTypeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, AssessmentType.values());
        spAssessmentType.setAdapter(assessmentTypeAdapter);
    }

    private AssessmentType getSpinnerValue() {
        return (AssessmentType) spAssessmentType.getSelectedItem();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AllViewsViewModel.class);

        mViewModel.mLiveAssessment.observe(this, assessment -> {
            if(assessment != null && !mEditing) {
                tvAssessmentTitle.setText(assessment.getTitle());
                tvAssessmentDate.setText(DateTextFormatting.fullDateFormat.format(assessment.getDate()));
                int position = getSpinnerPosition(assessment.getAssessmentType());
                spAssessmentType.setSelection(position);
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            setTitle(getString(R.string.new_assessment));
            mNewAssessment = true;
        } else if (extras.containsKey(COURSE_ID_KEY)){
            courseId = extras.getInt(TERM_ID_KEY);
            Log.v("DEBUG", "Extras course ID: " + courseId);
            setTitle(getString(R.string.new_assessment));
        } else {
            setTitle(R.string.edit_assessment);
            int assessmentId = extras.getInt(ASSESSMENT_ID_KEY);
            mViewModel.loadAssessmentData(assessmentId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNewAssessment) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private int getSpinnerPosition(AssessmentType assessmentType) {
        return assessmentTypeAdapter.getPosition(assessmentType);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if(item.getItemId() == R.id.action_delete) {
            mViewModel.deleteAssessment();
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    public void saveAndReturn() {
        try {
            Date date = DateTextFormatting.fullDateFormat.parse(tvAssessmentDate.getText().toString());
            mViewModel.saveAssessment(tvAssessmentTitle.getText().toString(), date, getSpinnerValue(), courseId);
            Log.v("Saved Assessment", tvAssessmentTitle.toString());
        } catch (ParseException e) {
            Log.v("Exception", e.getLocalizedMessage());
        }
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(EDITING_KEY, true);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.ass_edit_date_btn)
    public void assessmentDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            tvAssessmentDate.setText(DateTextFormatting.fullDateFormat.format(myCalendar.getTime()));
            startDateMills.setText(Long.toString(myCalendar.getTimeInMillis()));
            startAlarm(myCalendar);
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        /*if (c.before(Calendar.getInstance())) {
            c.add(Calendar.DATE, 1);
        }*/

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
        }
    }
}