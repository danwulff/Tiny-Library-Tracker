package com.epicodus.tinylibrarytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.signUpButton) Button mSignUpButton;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.favoritesButton) Button mFavoritesButton;
    @Bind(R.id.mainImage) ImageView mMainImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSignUpButton.setOnClickListener(this);
        mSearchButton.setOnClickListener(this);
        mFavoritesButton.setOnClickListener(this);

        Picasso.with(this).load(R.drawable.main).fit().centerCrop().into(mMainImage);
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
        else if (v == mSignUpButton) {
            Intent intent = new Intent (MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        }
    }
}
