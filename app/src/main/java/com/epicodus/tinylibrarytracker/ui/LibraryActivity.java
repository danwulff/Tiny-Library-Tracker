package com.epicodus.tinylibrarytracker.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.epicodus.tinylibrarytracker.Constants;
import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.models.Library;
import com.epicodus.tinylibrarytracker.services.CloudinaryServicev2;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.File;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class LibraryActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String TAG = CreateNewLibraryActivity.class.getSimpleName();

    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.libraryImageView) ImageView mLibraryImage;
    @Bind(R.id.libraryCharterNumber) TextView mCharterNumber;
    @Bind(R.id.libraryAddress) TextView mLibraryAddress;
    @Bind(R.id.latitude) TextView mLatitude;
    @Bind(R.id.longitude) TextView mLongitude;

    @Bind(R.id.uploadPhotoButton) Button mUploadPhotoButton;
    @Bind(R.id.addFavoriteButton) Button mAddFavoriteButton;

    ArrayList<Library> mLibraries = new ArrayList<>();
    Library library;

    private DatabaseReference mLibraryReference;

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
        if(library.getCharterNumber() != -1) {
            mCharterNumber.setText("Charter#: " + library.getCharterNumber());
        }
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
        mUploadPhotoButton.setOnClickListener(this);
        mAddFavoriteButton.setOnClickListener(this);
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
        } else if (v == mUploadPhotoButton) {
            takeImage();

        } else if (v == mAddFavoriteButton) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            String uid = user.getUid();
            DatabaseReference userFavoritesRef = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_FAVORITES)
                    .child(uid);
            userFavoritesRef.push().setValue(library.getPushId());

            Toast.makeText(LibraryActivity.this, "Saved", Toast.LENGTH_SHORT).show();
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

    public void uploadNewPhoto() {
        //allow thread for image upload
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        CloudinaryServicev2 uploadService = new CloudinaryServicev2();
        String url = uploadService.uploadPhoto(newPhotoUri);

        if(url == null) {
            //alert user to none address
            AlertDialog.Builder builder = new AlertDialog.Builder(LibraryActivity.this);
            builder.setMessage("Image failed to upload");
            builder.setCancelable(true);

            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog failedLibraryCreation = builder.create();
            failedLibraryCreation.show();
            return;
        } else {
            //push object to firebase
            mLibraryReference = FirebaseDatabase
                    .getInstance()
                    .getReference(Constants.FIREBASE_CHILD_LIBRARIES)
                    .child(library.getPushId());
            mLibraryReference.child("image").setValue(url);
        }
    }

    //TODO: Dry up with CreateNewLibrary code
    //----------------------------------------------------------------------------------------------
    //Take a picture
    //-----------
    static final int MY_PERMISSIONS_REQUEST_USE_EXTERNAL = 101;

    private void takeImage() {
        int permissionCheck = ContextCompat.checkSelfPermission(LibraryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(LibraryActivity.this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(LibraryActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(LibraryActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_USE_EXTERNAL);
                }
            }
        } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            dispatchTakePictureIntent();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    dispatchTakePictureIntent();

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    String photoFileName = "tinyLibraryPhoto.jpg";   //will overwrite previous picture in gallery at the moment, could create dynamic name?
    Uri newPhotoUri = null;

    public void dispatchTakePictureIntent() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getPhotoFileUri(photoFileName)); // set the image file name

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri takenPhotoUri = getPhotoFileUri(photoFileName);
                // Load the taken image into a preview
                Picasso.with(this).load(takenPhotoUri).fit().centerCrop().into(mLibraryImage);
                //save file Uri for reference elsewhere
                newPhotoUri = takenPhotoUri;
                //upload photo
                uploadNewPhoto();
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Returns the Uri for a photo stored on disk given the fileName
    public Uri getPhotoFileUri(String fileName) {
        // Only continue if the SD Card is mounted
        if (isExternalStorageAvailable()) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return Uri.fromFile(new File(mediaStorageDir.getAbsolutePath() + File.separator + fileName));
        }
        return null;
    }

    // Returns true if external storage for photos is available
    private boolean isExternalStorageAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }
}
