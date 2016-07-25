package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.createNewButton) Button mCreateNewButton;
    @Bind(R.id.listRecyclerView) RecyclerView mLibraryList;
    @Bind(R.id.title) TextView mTitle;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mCreateNewButton.setOnClickListener(this);

        Intent intent = getIntent();
        int zipCode =  Integer.parseInt(intent.getStringExtra("zipCode"));
        mTitle.setText("Search Results: " + zipCode);

    }

    @Override
    public void onClick(View v) {
        if(v == mCreateNewButton) {
            Intent intent = new Intent (SearchResultsActivity.this, CreateNewLibraryActivity.class);
            startActivity(intent);
        }
    }
}
