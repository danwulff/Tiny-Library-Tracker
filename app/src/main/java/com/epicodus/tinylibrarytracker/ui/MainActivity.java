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

    @Bind(R.id.signInButton) Button mSignInButton;
    @Bind(R.id.signUpButton) Button mSignUpButton;
    @Bind(R.id.emailInput) EditText mEmailInput;
    @Bind(R.id.passwordInput) EditText mPasswordInput;

    @Bind(R.id.userName) TextView mUserName;
    @Bind(R.id.searchInput) EditText mSearchInput;
    @Bind(R.id.searchButton) Button mSearchButton;
    @Bind(R.id.favoritesButton) Button mFavoritesButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mSignInButton.setOnClickListener(this);
        mSignUpButton.setOnClickListener(this);
        mUserName.setVisibility(View.INVISIBLE);
        mSearchInput.setVisibility(View.INVISIBLE);
        mSearchButton.setVisibility(View.INVISIBLE);
        mFavoritesButton.setVisibility(View.INVISIBLE);


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
        else if (v == mSignInButton) {
            //check that login was correct in the future
            if (true) {
                //show sign in message
                mUserName.setText("Signed in as: " + mEmailInput.getText().toString());
                mUserName.setVisibility(View.VISIBLE);
                //hide sign-in/sign-up
                mSignInButton.setVisibility(View.INVISIBLE);
                mSignUpButton.setVisibility(View.INVISIBLE);
                mEmailInput.clearFocus();
                mPasswordInput.clearFocus();
                mEmailInput.setVisibility(View.INVISIBLE);
                mPasswordInput.setVisibility(View.INVISIBLE);
                //hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                //show search & favorites
                mSearchInput.setVisibility(View.VISIBLE);
                mSearchButton.setVisibility(View.VISIBLE);
                mFavoritesButton.setVisibility(View.VISIBLE);
                mSearchButton.setOnClickListener(this);
                mFavoritesButton.setOnClickListener(this);
            } else {
                //login incorrect, display error
            }
        }
        else if (v == mSignUpButton) {
            Intent intent = new Intent (MainActivity.this, SignUpActivity.class);
            intent.putExtra("email", mEmailInput.getText().toString());
            intent.putExtra("password", mPasswordInput.getText().toString());
            startActivity(intent);
        }
    }
}
