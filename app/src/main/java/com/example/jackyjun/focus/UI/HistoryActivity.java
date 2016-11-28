package com.example.jackyjun.focus.UI;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.jackyjun.focus.DB.MyDatabase;
import com.example.jackyjun.focus.FinishedPlanAdapter;
import com.example.jackyjun.focus.Model.Plan;
import com.example.jackyjun.focus.R;

import java.util.List;

/**
 * Created by jackyjun on 16/11/24.
 */

public class HistoryActivity extends Activity{

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private FinishedPlanAdapter mAdapter;
    private List<Plan> planList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        loadRecyclerView();
    }

    private void loadRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.history_activity_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        MyDatabase db = MyDatabase.getInstance(this);
        planList = db.getAllFinishedPlan();

        mAdapter = new FinishedPlanAdapter(this, planList);
        mRecyclerView.setAdapter(mAdapter);
    }
}
