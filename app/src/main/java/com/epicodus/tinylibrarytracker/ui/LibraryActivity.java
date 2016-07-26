package com.epicodus.tinylibrarytracker.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.models.Library;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LibraryActivity extends AppCompatActivity implements View.OnClickListener{
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.libraryImageView) ImageView mLibraryImage;
    @Bind(R.id.libraryCharterNumber) TextView mCharterNumber;
    @Bind(R.id.libraryAddress) TextView mLibraryAddress;
    @Bind(R.id.latitude) TextView mLatitude;
    @Bind(R.id.longitude) TextView mLongitude;
    @Bind(R.id.uploadPhotoButton) Button mUploadPhoto;

    ArrayList<Library> mLibraries = new ArrayList<>();
    Library library;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library);
        ButterKnife.bind(this);

        mLibraries = Parcels.unwrap(getIntent().getParcelableExtra("libraries"));
        int position = Integer.parseInt(getIntent().getStringExtra("position"));


        library = mLibraries.get(position);
        //print info
        Picasso.with(this)
                .load(library.getImage())
                .fit()
                .centerCrop()
                .into(mLibraryImage);
        mCharterNumber.setText("Charter#: " + library.getCharterNumber());
        SpannableString content = new SpannableString(library.getAddress());
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        mLibraryAddress.setText(content);
        if(library.getLatitude() >= 0.0) {
            mLatitude.setText("Latitude: " + library.getLatitude() + " N");
        } else {
            mLatitude.setText("Latitude: " + -library.getLatitude() + " S");
        }

        if(library.getLongitude() >= 0.0) {
            mLongitude.setText("Longitude: " + library.getLongitude() + " E");
        } else {
            mLongitude.setText("Longitude: " + -library.getLongitude() + " W");
        }

        mLibraryAddress.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v == mLibraryAddress) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
//            String location = "geo:" + library.getLatitude() + "," + library.getLongitude() + "?q=(" + library.getCharterNumber() + ")";
            String location = "geo:0,0?q=" + library.getLatitude() + "," + library.getLongitude() + "(Charter%23+" + library.getCharterNumber() + ")";
            Log.d("location string", location);
            intent.setData(Uri.parse(location));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
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
