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
import database.RecyclerContext;
import database.TermAdapter;
import entity.Term;
import views.TermViewModel;

public class TermActivity extends AppCompatActivity {

    @BindView(R.id.term_recycler_view)
    RecyclerView mTermRecyclerView;

    @OnClick(R.id.fab)
    void fabClickHandler() {
        Intent intent = new Intent(this, TermAddEditActivity.class);
        startActivity(intent);
    }

    private List<Term> termData = new ArrayList<>();
    private TermAdapter mTermAdapter;
    private TermViewModel mTermViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_term_main);
        //Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        ButterKnife.bind(this);
        initRecyclerView();
        initViewModel();
    }

    private void initRecyclerView() {
        mTermRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mTermRecyclerView.setLayoutManager(layoutManager);
    }

    private void initViewModel() {
        final Observer<List<Term>> termObserver =
                termEntities -> {
                    termData.clear();
                    termData.addAll(termEntities);

                    if(mTermAdapter == null) {
                        mTermAdapter = new TermAdapter(termData, TermActivity.this, RecyclerContext.MAIN);
                        mTermRecyclerView.setAdapter(mTermAdapter);
                    } else {
                        mTermAdapter.notifyDataSetChanged();
                    }
                };
        mTermViewModel = ViewModelProviders.of(this).get(TermViewModel.class);
        mTermViewModel.mTerms.observe(this, termObserver);
    }
}

