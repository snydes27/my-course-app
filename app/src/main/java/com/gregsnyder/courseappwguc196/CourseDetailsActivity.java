package com.gregsnyder.courseappwguc196;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.AssessmentAdapter;
import database.AssessmentDropdownMenu;
import database.RecyclerContext;
import entity.Assessment;
import utilities.DateTextFormatting;
import views.AllViewsViewModel;

import static utilities.Constants.COURSE_ID_KEY;

public class CourseDetailsActivity extends AppCompatActivity implements AssessmentAdapter.AssessmentSelectedListener {
    @BindView(R.id.course_detail_start)
    TextView courseStartDate;

    @BindView(R.id.course_detail_end)
    TextView courseEndDate;

    @BindView(R.id.rview_course_detail_assessments)
    RecyclerView mAssRecyclerView;

    @BindView(R.id.course_detail_status)
    TextView tvCourseStatus;

    @BindView(R.id.course_detail_mentor)
    TextView tvCourseMentor;

    @BindView(R.id.course_detail_note)
    TextView tvCourseNote;

    @BindView(R.id.fab_add_assessment)
    FloatingActionButton fabAddAssessment;



    private List<Assessment> assessmentData = new ArrayList<>();
    private List<Assessment> unassignedAssessments = new ArrayList<>();
    private int courseId;
    private AssessmentAdapter mAssessmentAdapter;
    private AllViewsViewModel mViewModel;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_details);

        ButterKnife.bind(this);
        initAssRecyclerView();
        initViewModel();
    }

    private void initAssRecyclerView() {
        mAssRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mAssRecyclerView.setLayoutManager(layoutManager);

    }


    private void initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AllViewsViewModel.class);

        mViewModel.mLiveCourse.observe(this, course -> {
            courseStartDate.setText(DateTextFormatting.fullDateFormat.format(course.getStartDate()));
            courseEndDate.setText(DateTextFormatting.fullDateFormat.format(course.getAnticipatedEndDate()));
            tvCourseStatus.setText(course.getCourseStatus().toString());
            tvCourseNote.setText(course.getNote());
            tvCourseMentor.setText(course.getMentor());

            //SimpleDateFormat startDatesdf = new SimpleDateFormat("MM/dd/yyyy");
            //Date date = startDatesdf.parse(String.valueOf(courseStartDate));
            //long startDateToMillis = date.getTimeInMillis();

            //startDateMills.setText(Long.toString(startDateToMillis.getTimeInMillis()));


        });

        // Assessments
        final Observer<List<Assessment>> assessmentObserver =
                assessmentEntities -> {
                    assessmentData.clear();
                    assessmentData.addAll(assessmentEntities);

                    if (mAssessmentAdapter == null) {
                        mAssessmentAdapter = new AssessmentAdapter(assessmentData, CourseDetailsActivity.this, RecyclerContext.CHILD, this);
                        mAssRecyclerView.setAdapter(mAssessmentAdapter);
                    } else {
                        mAssessmentAdapter.notifyDataSetChanged();
                    }
                };
        final Observer<List<Assessment>> unassignedAssessmentObserver =
                assessmentEntities -> {
                    unassignedAssessments.clear();
                    unassignedAssessments.addAll(assessmentEntities);
                };


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            courseId = extras.getInt(COURSE_ID_KEY);
            mViewModel.loadCourseData(courseId);
        } else {
            finish();
        }

        mViewModel.getAssessmentsInCourse(courseId).observe(this, assessmentObserver);
        mViewModel.getUnassignedAssessments().observe(this, unassignedAssessmentObserver);

    }

    @OnClick(R.id.fab_edit_course)
    public void openEditActivity() {
        Intent intent = new Intent(this, CourseAddEditActivity.class);
        intent.putExtra(COURSE_ID_KEY, courseId);
        this.startActivity(intent);
        finish();
    }

    @OnClick(R.id.fab_add_assessment)
    public void assessmentAddButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add new or existing assessment?");
        builder.setMessage("Please add new or existing assessment");
        builder.setIcon(R.drawable.ic_add);
        builder.setPositiveButton("New", (dialog, id) -> {
            dialog.dismiss();
            Intent intent = new Intent(this, AssessmentAddEditActivity.class);
            intent.putExtra(COURSE_ID_KEY, courseId);
            this.startActivity(intent);
        });
        builder.setNegativeButton("Existing", (dialog, id) -> {
            if (unassignedAssessments.size() >= 1) {
                final AssessmentDropdownMenu menu = new AssessmentDropdownMenu(this, unassignedAssessments);
                menu.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
                menu.setWidth(getPxFromDp(200));
                menu.setOutsideTouchable(true);
                menu.setFocusable(true);
                menu.showAsDropDown(fabAddAssessment);
                menu.setAssessmentSelectedListener((position, assessment) -> {
                    menu.dismiss();
                    assessment.setCourseId(courseId);
                    mViewModel.overwriteAssessment(assessment, courseId);
                });
            } else {
                Toast.makeText(getApplicationContext(), "Create a new assessment.", Toast.LENGTH_SHORT).show();
            }

        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private int getPxFromDp(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onAssessmentSelected(int position, Assessment assessment) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Assessment?");
        builder.setMessage("This will remove assessment from current course.");
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Continue", (dialog, id) -> {
            dialog.dismiss();
            mViewModel.overwriteAssessment(assessment, -1);
            mAssessmentAdapter.notifyDataSetChanged();

        });
        builder.setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sharing) {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT,tvCourseNote.getText());
            // (Optional) Here we're setting the title of the content
            sendIntent.putExtra(Intent.EXTRA_TITLE, "Send message title");
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}