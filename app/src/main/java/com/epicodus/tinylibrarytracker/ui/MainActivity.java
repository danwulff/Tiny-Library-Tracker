package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;
import com.google.firebase.auth.FirebaseAuth;
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

        mSearchButton.setOnClickListener(this);
        mFavoritesButton.setOnClickListener(this);

        Picasso.with(this).load(R.drawable.library3).fit().centerCrop().into(mMainImage);

        mUserName.setText("Signed in as: " + "placeholder");

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
