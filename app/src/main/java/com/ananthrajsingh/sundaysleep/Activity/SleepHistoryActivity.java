package com.ananthrajsingh.sundaysleep.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ananthrajsingh.sundaysleep.AppDatabase;
import com.ananthrajsingh.sundaysleep.SleepDao;
import com.ananthrajsingh.sundaysleep.R;
import com.ananthrajsingh.sundaysleep.SleepHistoryAdapter;

public class SleepHistoryActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_history);

        mRecyclerView = findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        SleepHistoryAdapter sleepHistoryAdapter = new SleepHistoryAdapter(getBaseContext());
        mRecyclerView.setAdapter(sleepHistoryAdapter);


        AppDatabase db = AppDatabase.getDatabase(getApplicationContext());
        SleepDao sleepDao = db.sleepDao();
        sleepHistoryAdapter.swapList(sleepDao.getAll());


    }
}
