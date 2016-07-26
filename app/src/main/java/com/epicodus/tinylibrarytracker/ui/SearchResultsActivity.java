package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.Constants;
import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.adapters.LibraryListAdapter;
import com.epicodus.tinylibrarytracker.models.Library;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchResultsActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.createNewButton) Button mCreateNewButton;
    @Bind(R.id.listRecyclerView) RecyclerView mLibraryList;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.resultQuantity) TextView mResultsQuantity;
    @Bind(R.id.noLibrariesExistTextView) TextView mNoLibrariesTextView;

    private DatabaseReference mLibraryReference;
    private DatabaseReference mZipCodeReference;

    private LibraryListAdapter mListAdapter;
    private ArrayList<Library> mLibraries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        mNoLibrariesTextView.setVisibility(View.INVISIBLE);
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
        final ArrayList<String> libraryIds = new ArrayList<>();

        mZipCodeReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_ZIPCODES)
                .child(zipCode);
        mLibraryReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_LIBRARIES);

        mZipCodeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot zipCode : dataSnapshot.getChildren()) {
                        //id of library in zip code
                        Log.d("library id", zipCode.getValue().toString());
                        libraryIds.add(zipCode.getValue().toString());
                    }

                    //print amount of libraries found
                    mResultsQuantity.setText(libraryIds.size() + " Results Found");

                    //add single event listener to libraries
                    mLibraryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //for each id in zip code
                            for(int i = 0; i < libraryIds.size(); i++) {
                                mLibraries.add(dataSnapshot.child(libraryIds.get(i)).getValue(Library.class));
                                Log.d("for loop, push Id", dataSnapshot.child(libraryIds.get(i)).getValue(Library.class).getPushId());
                            }

                            //fill recyclerview
                            SearchResultsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListAdapter = new LibraryListAdapter(getApplicationContext(), mLibraries);
                                    mLibraryList.setAdapter(mListAdapter);
                                    RecyclerView.LayoutManager layoutManager =
                                            new LinearLayoutManager(SearchResultsActivity.this);
                                    mLibraryList.setLayoutManager(layoutManager);
                                    mLibraryList.setHasFixedSize(true);
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    mResultsQuantity.setText("0 Results Found");
                    mNoLibrariesTextView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
