package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.adapters.LibraryListAdapter;
import com.epicodus.tinylibrarytracker.models.Library;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.createNewButton) Button mCreateNewButton;
    @Bind(R.id.listRecyclerView) RecyclerView mLibraryList;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.resultQuantity) TextView mResultsQuantity;
    @Bind(R.id.listRecyclerView) RecyclerView mListRecyclerView;

    private LibraryListAdapter mListAdapter;
    public ArrayList<Library> mLibraries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mCreateNewButton.setOnClickListener(this);

        Intent intent = getIntent();
        String zipCode =  intent.getStringExtra("zipCode");
        mTitle.setText("Search Results: " + zipCode);

        getLibraries(zipCode);
    }

    @Override
    public void onClick(View v) {
        if(v == mCreateNewButton) {
            Intent intent = new Intent (SearchResultsActivity.this, CreateNewLibraryActivity.class);
            startActivity(intent);
        }
    }

    private void getLibraries(String zipCode) {
        //get arrayList of libraries from firebase
        //for each item in zip code
            Library library = new Library();
            mLibraries.add(library);

        //fill recyclerview
        /*SearchResultsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mListAdapter = new LibraryListAdapter(getApplicationContext(), mLibraries);
                mListRecyclerView.setAdapter(mListAdapter);
                RecyclerView.LayoutManager layoutManager =
                        new LinearLayoutManager(SearchResultsActivity.this);
                mListRecyclerView.setLayoutManager(layoutManager);
                mListRecyclerView.setHasFixedSize(true);
            }
        });*/

        //print amount of libraries found
        int amount = 3;
        mResultsQuantity.setText(amount + " Results Found");
    }
}
