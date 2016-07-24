package com.epicodus.tinylibrarytracker.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.mainImage) ImageView mMainImage;

    @Bind(R.id.userName) TextView mUserName;
    @Bind(R.id.searchInput) EditText mSearchInput;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.favoritesButton) Button mFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        mUserName.setVisibility(View.INVISIBLE);
//        mSearchInput.setVisibility(View.INVISIBLE);
//        mSearchButton.setVisibility(View.INVISIBLE);
//        mFavoritesButton.setVisibility(View.INVISIBLE);

        mSearchButton.setOnClickListener(this);
        mFavoritesButton.setOnClickListener(this);


        Picasso.with(this).load(R.drawable.main).fit().centerCrop().into(mMainImage);


        mUserName.setText("Signed in as: " + "placeholder");
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchInput.getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        if(v == mSearchButton) {
            Intent intent = new Intent (MainActivity.this, SearchResultsActivity.class);
            startActivity(intent);
        }
        else if (v == mFavoritesButton) {
            Intent intent = new Intent (MainActivity.this, FavoriteLibrariesActivity.class);
            startActivity(intent);
        }
    }
}
