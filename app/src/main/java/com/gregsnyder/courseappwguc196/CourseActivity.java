package com.gregsnyder.courseappwguc196;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import database.CourseAdapter;
import database.RecyclerContext;
import entity.Course;
import views.CourseViewModel;

public class CourseActivity extends AppCompatActivity implements CourseAdapter.CourseSelectedListener {
    @BindView(R.id.course_recycler_view)
    RecyclerView mCourseRecyclerView;

    @OnClick(R.id.course_fab)
    void fabClickHandler() {
        Intent intent = new Intent(this, CourseAddEditActivity.class);
        startActivity(intent);
    }

    private List<Course> courseData = new ArrayList<>();
    private CourseAdapter mCourseAdapter;
    private CourseViewModel mCourseViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_main);

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();
    }

    private void initRecyclerView() {
        mCourseRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCourseRecyclerView.setLayoutManager(layoutManager);
    }

    private void initViewModel() {
        final Observer<List<Course>> courseObserver =
                courseEntities -> {
                    courseData.clear();
                    courseData.addAll(courseEntities);

                    if(mCourseAdapter == null) {
                        mCourseAdapter = new CourseAdapter(courseData, CourseActivity.this, RecyclerContext.MAIN, this);
                        mCourseRecyclerView.setAdapter(mCourseAdapter);
                    } else {
                        mCourseAdapter.notifyDataSetChanged();
                    }
                };
        mCourseViewModel = ViewModelProviders.of(this).get(CourseViewModel.class);
        mCourseViewModel.mCourses.observe(this, courseObserver);
    }

    @Override
    public void onCourseSelected(int position, Course course) {

    }
}