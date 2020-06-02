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
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.CourseStatus;
import utilities.DateTextFormatting;
import views.AllViewsViewModel;

import static utilities.Constants.COURSE_ID_KEY;
import static utilities.Constants.EDITING_KEY;
import static utilities.Constants.TERM_ID_KEY;

public class CourseAddEditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    @BindView(R.id.course_edit_title)
    EditText tvCourseTitle;

    @BindView(R.id.course_edit_start)
    EditText tvCourseStartDate;

    @BindView(R.id.course_edit_end)
    EditText tvCourseEndDate;

    @BindView(R.id.course_edit_start_btn)
    ImageButton btnStartDate;

    @BindView(R.id.course_edit_end_btn)
    ImageButton btnEndDate;

    @BindView(R.id.course_edit_status_dropdown)
    Spinner spCourseStatus;

    @BindView(R.id.course_edit_note)
    EditText tvNote;

    @BindView(R.id.mentors_spinner)
    Spinner tvMentor;

    @BindView(R.id.start_DateInMills_course)
    TextView startDateMills;

    @BindView(R.id.end_DateInMills_course)
    TextView endDateMills;


    private AllViewsViewModel mViewModel;
    private boolean mNewCourse, mEditing;
    private int termId = -1;
    private ArrayAdapter<CourseStatus> courseStatusAdapter;
    private Executor executor = Executors.newSingleThreadExecutor();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course_add_edit);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_save);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }
        initViewModel();
        // Set up spinner object
        addSpinnerItems();

        Spinner mentorSpinner = findViewById(R.id.mentors_spinner);
        ArrayAdapter<CharSequence> mentorAdapter = ArrayAdapter.createFromResource(this,
                R.array.mentors_array, android.R.layout.simple_spinner_item);
        mentorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mentorSpinner.setAdapter(mentorAdapter);
        mentorSpinner.setOnItemSelectedListener(this);


        /*picker=(CalendarView)findViewById(R.id.calendarView);
        picker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                Calendar c = Calendar.getInstance();
                c.set(year, month, dayOfMonth, 0, 0);
                mills.setText(Long.toString(c.getTimeInMillis()));
            }
        });*/
    }

    private void addSpinnerItems() {
        courseStatusAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, CourseStatus.values());
        spCourseStatus.setAdapter(courseStatusAdapter);
    }

    private CourseStatus getSpinnerValue() {
        return (CourseStatus) spCourseStatus.getSelectedItem();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AllViewsViewModel.class);

        mViewModel.mLiveCourse.observe(this, course -> {
            if(course != null && !mEditing) {
                tvCourseTitle.setText(course.getTitle());
                tvCourseStartDate.setText(DateTextFormatting.fullDateFormat.format(course.getStartDate()));
                tvCourseEndDate.setText(DateTextFormatting.fullDateFormat.format(course.getAnticipatedEndDate()));
                tvNote.setText(course.getNote());
                int position = getSpinnerPosition(course.getCourseStatus());
                spCourseStatus.setSelection(position);
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            setTitle(getString(R.string.new_course));
            mNewCourse = true;
        } else if (extras.containsKey(TERM_ID_KEY)){ // Check if this is adding a course to a term
            termId = extras.getInt(TERM_ID_KEY);
            Log.v("DEBUG", "Extras term ID: " + termId);
            setTitle(getString(R.string.new_course));
        } else {
            setTitle(getString(R.string.edit_course));
            int courseId = extras.getInt(COURSE_ID_KEY);
            mViewModel.loadCourseData(courseId);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNewCourse) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private int getSpinnerPosition(CourseStatus courseStatus) {
        return courseStatusAdapter.getPosition(courseStatus);
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    public void saveAndReturn() {
        try {
            Date startDate = DateTextFormatting.fullDateFormat.parse(tvCourseStartDate.getText().toString());
            Date endDate = DateTextFormatting.fullDateFormat.parse(tvCourseEndDate.getText().toString());
            mViewModel.saveCourse(tvCourseTitle.getText().toString(), startDate, endDate, getSpinnerValue(), termId, tvNote.getText().toString(), tvMentor.getSelectedItem().toString());
            Log.v("Saved Course", tvCourseTitle.toString());
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

    @OnClick(R.id.course_edit_start_btn)
    public void courseStartDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            tvCourseStartDate.setText(DateTextFormatting.fullDateFormat.format(myCalendar.getTime()));
            startDateMills.setText(Long.toString(myCalendar.getTimeInMillis()));
            startAlarm(myCalendar);
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.course_edit_end_btn)
    public void courseEndDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            endDateMills.setText(Long.toString(myCalendar.getTimeInMillis()));
            tvCourseEndDate.setText(DateTextFormatting.fullDateFormat.format(myCalendar.getTime()));

            startAlarm(myCalendar);
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        /*if (id == R.id.notification_button) {
            Intent intent=new Intent(CourseAddEditActivity.this,AppReceiver.class);
            intent.putExtra("key","This is a short message");
            PendingIntent sender= PendingIntent.getBroadcast(CourseAddEditActivity.this,0,intent,0);
            AlarmManager alarmManager=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
            startDateNotification =Long.parseLong(startDateMills.getText().toString());
            endDateNotification =Long.parseLong(endDateMills.getText().toString());

            alarmManager.set(AlarmManager.RTC_WAKEUP, startDateNotification, sender);
            alarmManager.set(AlarmManager.RTC_WAKEUP, endDateNotification, sender);
            //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+300, sender);
            return true;
        }*/
        if(item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if(item.getItemId() == R.id.action_delete) {
            mViewModel.deleteCourse();
            finish();
        }

        return super.onOptionsItemSelected(item);
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
