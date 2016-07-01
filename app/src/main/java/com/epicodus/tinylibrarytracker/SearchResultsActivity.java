package com.epicodus.tinylibrarytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.libraryButton) Button mLibraryButton;
    @Bind(R.id.createNewButton) Button mCreateNewButton;
    @Bind(R.id.libraryList) ListView mLibraryList;
    private String[] libraries = new String[] {"Stephen's tardis box", "Clara's library", "Doug's book hut", "Dan's library"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mLibraryButton.setOnClickListener(this);
        mCreateNewButton.setOnClickListener(this);

        mLibraryList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, libraries));
    }

    @Override
    public void onClick(View v) {
        if(v == mLibraryButton) {
            Intent intent = new Intent (SearchResultsActivity.this, LibraryActivity.class);
            startActivity(intent);
        }
        if(v == mCreateNewButton) {
            Intent intent = new Intent (SearchResultsActivity.this, CreateNewLibraryActivity.class);
            startActivity(intent);
        }
    }
}
