package com.epicodus.tinylibrarytracker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.Constants;
import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.adapters.LibraryListAdapter;
import com.epicodus.tinylibrarytracker.models.Library;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FavoriteLibrariesActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.listRecyclerView) RecyclerView mLibraryList;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.resultQuantity) TextView mResultsQuantity;
    @Bind(R.id.noLibrariesExistTextView) TextView mNoLibrariesTextView;

    private DatabaseReference mLibraryReference;
    private DatabaseReference mFavoritesReference;

    private ProgressDialog mAuthProgressDialog;

    private LibraryListAdapter mListAdapter;
    private ArrayList<Library> mLibraries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        ButterKnife.bind(this);

        createAuthProgressDialog();
        mAuthProgressDialog.show();

        mNoLibrariesTextView.setVisibility(View.INVISIBLE);

        getFavoriteLibraries();
    }

    @Override
    public void onClick(View v) {

    }


    private void getFavoriteLibraries() {
        final ArrayList<String> libraryIds = new ArrayList<>();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        mFavoritesReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_FAVORITES)
                .child(uid);
        mLibraryReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_LIBRARIES);

        mFavoritesReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for(DataSnapshot favorite : dataSnapshot.getChildren()) {
                        //id of library in zip code
                        Log.d("favorite library id", favorite.getValue().toString());
                        libraryIds.add(favorite.getValue().toString());
                    }

                    //print amount of libraries found
                    mResultsQuantity.setText(libraryIds.size() + " Results Found");

                    //add single event listener to libraries
                    mLibraryReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            //for each id
                            for(int i = 0; i < libraryIds.size(); i++) {
                                mLibraries.add(dataSnapshot.child(libraryIds.get(i)).getValue(Library.class));
                                Log.d("for loop, push Id", dataSnapshot.child(libraryIds.get(i)).getValue(Library.class).getPushId());
                            }

                            //fill recyclerview
                            FavoriteLibrariesActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mListAdapter = new LibraryListAdapter(getApplicationContext(), mLibraries);
                                    mLibraryList.setAdapter(mListAdapter);
                                    RecyclerView.LayoutManager layoutManager =
                                            new LinearLayoutManager(FavoriteLibrariesActivity.this);
                                    mLibraryList.setLayoutManager(layoutManager);
                                    mLibraryList.setHasFixedSize(true);
                                }
                            });

                            mAuthProgressDialog.hide();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
                else {
                    mResultsQuantity.setText("0 Results Found");
                    mNoLibrariesTextView.setVisibility(View.VISIBLE);
                    mAuthProgressDialog.hide();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Loading Libraries from Database...");
        mAuthProgressDialog.setCancelable(false);
    }
}
