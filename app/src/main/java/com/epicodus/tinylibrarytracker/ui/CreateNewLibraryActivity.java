package com.epicodus.tinylibrarytracker.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.tinylibrarytracker.Constants;
import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.models.Library;
import com.epicodus.tinylibrarytracker.services.CloudinaryService;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateNewLibraryActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String TAG = CreateNewLibraryActivity.class.getSimpleName();

    @Bind(R.id.backgroundLayout) View mBackgroundLayout;
    @Bind(R.id.selectPhotoButton) Button mSelectPhotoButton;
    @Bind(R.id.newLibraryButton) Button mNewLibraryButton;
    @Bind(R.id.placeholderImage) ImageView mPlaceholderImage;

    @Bind(R.id.charterInput) EditText mCharterInput;
    @Bind(R.id.locationSpinner) Spinner mLocationSpinner;
    @Bind(R.id.addressInput) EditText mAddressInput;
    @Bind(R.id.zipCodeInput) EditText mZipCodeInput;
    @Bind(R.id.latitudeInput) EditText mLatitudeInput;
    @Bind(R.id.longitudeInput) EditText mLongitudeInput;

    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private DatabaseReference mLibraryReference;
    private DatabaseReference mZipCodeReference;
    private ProgressDialog mAuthProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_library);
        ButterKnife.bind(this);

        //set background image
        View backgroundimage = mBackgroundLayout;
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(80);

        //hide input forms for drop down menu use
        mAddressInput.setVisibility(View.GONE);
        mZipCodeInput.setVisibility(View.GONE);
        mLatitudeInput.setVisibility(View.GONE);
        mLongitudeInput.setVisibility(View.GONE);
        //build spinner
        addItemsOnSpinner(mLocationSpinner);
        mLocationSpinner.setOnItemSelectedListener(this);

        //google api client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mSelectPhotoButton.setOnClickListener(this);
        mNewLibraryButton.setOnClickListener(this);
        createAuthProgressDialog();
    }

    // add items into spinner dynamically
    public void addItemsOnSpinner(android.widget.Spinner spinner) {
        List<String> list = new ArrayList<String>();
        list.add("Select One:");
        list.add("Enter Address");
        list.add("Get Current Location");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
        String selected = parent.getItemAtPosition(position).toString();
        if(selected == "Select One:") {
            //do nothing
        } else if (selected == "Enter Address") {
            getAddressPrompt(this);
        } else if (selected == "Get Current Location") {
            //calls google services
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        //do nothing
    }


    @Override
    public void onClick(View view) {
        if(view == mSelectPhotoButton){
            takeImage();
        } else if (view == mNewLibraryButton) {
            //check to make sure all fields having proper input
            if(newPhotoUri == null) {
                Toast.makeText(CreateNewLibraryActivity.this, "Please Take a Photo", Toast.LENGTH_LONG).show();
            }
            else if(mAddressInput.getText().toString().length() == 0 && mAddressInput.getText().toString() != "Address") {
                if(mAddressInput.isShown()) {
                    mAddressInput.setError("Please enter an Address");
                } else {
                    Toast.makeText(CreateNewLibraryActivity.this, "Please Choose a Location", Toast.LENGTH_LONG).show();
                }
            }
            else if(mZipCodeInput.getText().toString().length() != 5 && mZipCodeInput.getText().toString() != "Zip Code") {
                if(mZipCodeInput.isShown()) {
                    mZipCodeInput.setError("Please enter a 5-digit Zip Code");
                } else {
                    Toast.makeText(CreateNewLibraryActivity.this, "Please Choose a Location", Toast.LENGTH_LONG).show();
                }
            }
            else if(mLatitudeInput.getText().toString().length() == 0 && mLatitudeInput.getText().toString() != "Latitude") {
                if(mLatitudeInput.isShown()) {
                    mLatitudeInput.setError("Please enter a latitude");
                } else {
                    Toast.makeText(CreateNewLibraryActivity.this, "Please Choose a Location", Toast.LENGTH_LONG).show();
                }
            }
            else if(mLongitudeInput.getText().toString().length() == 0 && mLongitudeInput.getText().toString() != "Longitude") {
                if(mLongitudeInput.isShown()) {
                    mLongitudeInput.setError("Please enter a longitude");
                } else {
                    Toast.makeText(CreateNewLibraryActivity.this, "Please Choose a Location", Toast.LENGTH_LONG).show();
                }
            }
            else {
                mAuthProgressDialog.show();
                createLibrary();
                mAuthProgressDialog.hide();
            }
        }
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Creating New Library...");
        mAuthProgressDialog.setCancelable(false);
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

    public void setFields(Address address) {
        mAddressInput.setText(address.getAddressLine(0) + " " + address.getLocality() +  " " + address.getAdminArea());
        mZipCodeInput.setText(address.getPostalCode());
        mLatitudeInput.setText(String.valueOf(address.getLatitude()));
        mLongitudeInput.setText(String.valueOf(address.getLongitude()));

        mAddressInput.setVisibility(View.VISIBLE);
        mZipCodeInput.setVisibility(View.VISIBLE);
        mLatitudeInput.setVisibility(View.VISIBLE);
        mLongitudeInput.setVisibility(View.VISIBLE);

        mLocationSpinner.setSelection(0);
    }

    //----------------------------------------------------------------------------------------------
    //getCoordinates from address
    //-----------

    public void getAddressPrompt(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter an Address");


        // Set up the input
        final EditText input = new EditText(context);
        // Specify the type of input expected
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String address = input.getText().toString();
                if(!TextUtils.isEmpty(address)) {
                    getCoordinatesFromAddress(context, address);
                } else {
                    noAddressDialog(context);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mLocationSpinner.setSelection(0);
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void getCoordinatesFromAddress(Context context, String address) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocationName(address, 1);

            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                setFields(returnedAddress);
            }
            else {
               noAddressDialog(context);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void noAddressDialog(Context context) {
        //alert user to none address
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No Address Was Found");
        builder.setCancelable(true);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        mLocationSpinner.setSelection(0);
                        dialog.cancel();
                    }
                });

        AlertDialog noAddressAlert = builder.create();
        noAddressAlert.show();
    }


    //----------------------------------------------------------------------------------------------
    //getCurrentLocation
    //-----------

    @Override
    public void onConnectionFailed(ConnectionResult arg0) {
        Toast.makeText(this, "Failed to connect...", Toast.LENGTH_SHORT).show();
    }

    static final int MY_PERMISSIONS_REQUEST_USE_FINE_LOCATION = 100;

    @Override
    public void onConnected(Bundle arg0) {
        int permissionCheck = ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "permission denied, ask for permission");
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(CreateNewLibraryActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(CreateNewLibraryActivity.this,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSIONS_REQUEST_USE_FINE_LOCATION);
                }
            }
        } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            getCurrentLocation();
        }
    }

    @Override
    public void onConnectionSuspended(int arg0) {
        Toast.makeText(this, "Connection suspended...", Toast.LENGTH_SHORT).show();

    }

    public void getCurrentLocation() {
        if (mLastLocation != null) {
            //done getting location
            mGoogleApiClient.disconnect();
            //get address at that location
            Geocoder geocoder = new Geocoder(CreateNewLibraryActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    setFields(returnedAddress);
                }
                else {
                    noAddressDialog(CreateNewLibraryActivity.this);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



    //----------------------------------------------------------------------------------------------
    //Take a picture
    //-----------
    static final int MY_PERMISSIONS_REQUEST_USE_EXTERNAL = 101;

    private void takeImage() {
            int permissionCheck = ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                // Here, thisActivity is the current activity
                if (ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(CreateNewLibraryActivity.this,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    } else {
                        // No explanation needed, we can request the permission.
                        ActivityCompat.requestPermissions(CreateNewLibraryActivity.this,
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
            case MY_PERMISSIONS_REQUEST_USE_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //appease the android gods
                    int permissionCheck = ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

                    if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
                        getCurrentLocation();
                    }
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
                Picasso.with(this).load(takenPhotoUri).fit().centerCrop().into(mPlaceholderImage);
                //save file Uri for reference elsewhere
                newPhotoUri = takenPhotoUri;
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

    //----------------------------------------------------------------------------------------------
    //Create Library
    //--------------

    private void createLibrary() {

        saveLibraryToFirebase("https://placeholdit.imgix.net/~text?txtsize=28&bg=0099ff&txtclr=ffffff&txt=300%C3%97300&w=300&h=300&fm=png");

        Intent intent = new Intent(CreateNewLibraryActivity.this, SearchResultsActivity.class);
        intent.putExtra("zipCode", mZipCodeInput.getText().toString());
        startActivity(intent);

        /*Log.d(TAG, "createLibrary()");
        //for now, upload photo to api
        final CloudinaryService cloudinaryService = new CloudinaryService();

        //move bitmap conversion to here from service to reduce cpu load on thread
        // image, convert to Base64 string
        Log.d("create library", "bitmapfactory.decodefile");
        Bitmap img = BitmapFactory.decodeFile(newPhotoUri.toString().replace("file:", ""));

        Log.d("create library", "cloudinaryservice.convertbitmaptoString()");
        String imgString = CloudinaryService.convertBitmapToString(img);

        Log.d("create library", "upload photo");
        cloudinaryService.uploadPhoto(imgString, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                String jsonData = "nope";
                try {
                    jsonData = response.body().string();
                    //TODO: check that jsonData saved correctly before saving to firebase

                    saveLibraryToFirebase("https://placeholdit.imgix.net/~text?txtsize=28&bg=0099ff&txtclr=ffffff&txt=300%C3%97300&w=300&h=300&fm=png");

                    Intent intent = new Intent(CreateNewLibraryActivity.this, SearchResultsActivity.class);
                    intent.putExtra("zipCode", mZipCodeInput.getText().toString());
                    startActivity(intent);

                } catch (Exception e) {
                    e.printStackTrace();
                    //TODO: add dialogue if creation failed
                }

                Log.d(TAG + "onResponse ", jsonData);
            }
        });*/


        //eventually publish library object to firebase with photo URL from API call,
        //instead, as a placeholder, call new activity and pass url, to see url loaded from internet url
    }

    private void saveLibraryToFirebase(String imageUrl) {

        int charterNumber;

        //create library object
        String charterString = mCharterInput.getText().toString();
        if(!TextUtils.isEmpty(charterString)) {
            charterNumber = Integer.parseInt(mCharterInput.getText().toString());
        } else {
            charterNumber = -1;
        }
        String address = mAddressInput.getText().toString();
        int zipCode = Integer.parseInt(mZipCodeInput.getText().toString());
        double latitude = Double.parseDouble(mLatitudeInput.getText().toString());
        double longitude = Double.parseDouble(mLongitudeInput.getText().toString());
        Library newLibrary = new Library(charterNumber, zipCode, latitude, longitude, address, imageUrl);

        //push object to firebase
        mLibraryReference = FirebaseDatabase
                .getInstance()
                .getReference()
                .child(Constants.FIREBASE_CHILD_LIBRARIES);
        DatabaseReference pushRef = mLibraryReference.push();
        String pushId = pushRef.getKey();
        newLibrary.setPushId(pushId);
        pushRef.setValue(newLibrary);

        //push unique id into proper zip code for searching
        mZipCodeReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_ZIPCODES)
                .child(String.valueOf(zipCode));
        mZipCodeReference.push().setValue(pushId);

        //TODO: create reference for geofire searching
    }
}
