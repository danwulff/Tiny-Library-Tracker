package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.models.Library;

import org.parceler.Parcels;

import java.util.ArrayList;

public class LibraryActivity extends AppCompatActivity {
    ArrayList<Library> mLibraries = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);

        mLibraries = Parcels.unwrap(getIntent().getParcelableExtra("libraries"));
        int startingPosition = Integer.parseInt(getIntent().getStringExtra("position"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return (super.onOptionsItemSelected(menuItem));
    }
}
