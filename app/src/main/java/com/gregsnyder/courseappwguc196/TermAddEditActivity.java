package com.gregsnyder.courseappwguc196;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import entity.Course;
import utilities.DateTextFormatting;
import views.AllViewsViewModel;

import static utilities.Constants.EDITING_KEY;
import static utilities.Constants.TERM_ID_KEY;

public class TermAddEditActivity extends AppCompatActivity {
    @BindView(R.id.term_edit_title)
    EditText tvTermTitle;

    @BindView(R.id.term_edit_start)
    EditText tvTermStartDate;

    @BindView(R.id.term_edit_end)
    EditText tvTermEndDate;

    @BindView(R.id.term_edit_start_btn)
    ImageButton btnStartDate;

    @BindView(R.id.term_edit_end_btn)
    ImageButton btnEndDate;

    private AllViewsViewModel mViewModel;
    private boolean mNewTerm, mEditing;
    private List<Course> courseData = new ArrayList<>();
    int termId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_term_add_edit);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        // setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setHomeAsUpIndicator(R.drawable.ic_save);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        if(savedInstanceState != null) {
            mEditing = savedInstanceState.getBoolean(EDITING_KEY);
        }
        initViewModel();
    }

    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AllViewsViewModel.class);

        mViewModel.mLiveTerm.observe(this, term -> {
            if(term != null && !mEditing) {
                tvTermTitle.setText(term.getTitle());
                tvTermStartDate.setText(DateTextFormatting.fullDateFormat.format(term.getStartDate()));
                tvTermEndDate.setText(DateTextFormatting.fullDateFormat.format(term.getEndDate()));
            }
        });

        Bundle extras = getIntent().getExtras();
        if(extras == null) {
            setTitle(getString(R.string.new_term));
            mNewTerm = true;
        } else {
            setTitle(getString(R.string.edit_term));
            termId = extras.getInt(TERM_ID_KEY);
            mViewModel.loadTermData(termId);
        }

        final Observer<List<Course>> courseObserver =
                courseEntities -> {
                    courseData.clear();
                    courseData.addAll(courseEntities);
                };

        mViewModel.getCoursesInTerm(termId).observe(this, courseObserver);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(!mNewTerm) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_editor, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            saveAndReturn();
            return true;
        } else if (item.getItemId() == R.id.action_delete) {
            handleDelete();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleDelete() {
        if(mViewModel.mLiveTerm.getValue() != null) {
            String termTitle = mViewModel.mLiveTerm.getValue().getTitle();
            if(courseData != null && courseData.size() != 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete " + termTitle + "?");
                builder.setMessage("You are deleting term '" + termTitle + "'." +
                        "\nCourses assigned to it but these courses will not be deleted");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Yes", (dialog, id) -> {
                    dialog.dismiss();
                    mViewModel.deleteTerm();
                    finish();
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Delete " + termTitle + "?");
                builder.setMessage("Delete term '" + termTitle + "'?");
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setPositiveButton("Yes", (dialog, id) -> {
                    dialog.dismiss();
                    mViewModel.deleteTerm();
                    finish();
                });
                builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        }
    }

    @Override
    public void onBackPressed() {
        saveAndReturn();
    }

    public void saveAndReturn() {
        try {
            Date startDate = DateTextFormatting.fullDateFormat.parse(tvTermStartDate.getText().toString());
            Date endDate = DateTextFormatting.fullDateFormat.parse(tvTermEndDate.getText().toString());
            mViewModel.saveTerm(tvTermTitle.getText().toString(), startDate, endDate);
            Log.v("Saved term",tvTermTitle.toString());

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

    /**
     *
     */
    @OnClick(R.id.term_edit_start_btn)
    public void termStartDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            tvTermStartDate.setText(DateTextFormatting.fullDateFormat.format(myCalendar.getTime()));
            //startDateMills.setText(Long.toString(myCalendar.getTimeInMillis()));
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    @OnClick(R.id.term_edit_end_btn)
    public void termEndDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            tvTermEndDate.setText(DateTextFormatting.fullDateFormat.format(myCalendar.getTime()));
        };
        new DatePickerDialog(this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

}

