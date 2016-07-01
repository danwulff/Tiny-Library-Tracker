package com.epicodus.tinylibrarytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.libraryButton) Button mLibraryButton;
    @Bind(R.id.createNewButton) Button mCreateNewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mLibraryButton.setOnClickListener(this);
        mCreateNewButton.setOnClickListener(this);
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
