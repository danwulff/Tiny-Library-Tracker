package com.epicodus.tinylibrarytracker.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.tinylibrarytracker.Constants;
import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.adapters.LibraryListAdapter;
import com.epicodus.tinylibrarytracker.models.Library;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

public class GeofireResultsActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener{
    public static final String TAG = CreateNewLibraryActivity.class.getSimpleName();

    @Bind(R.id.listRecyclerView) RecyclerView mLibraryList;
    @Bind(R.id.title) TextView mTitle;
    @Bind(R.id.resultQuantity) TextView mResultsQuantity;
    @Bind(R.id.noLibrariesExistTextView) TextView mNoLibrariesTextView;

    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation;

    private DatabaseReference mLibraryReference;
    private DatabaseReference mZipCodeReference;

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

        //google api client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        mGoogleApiClient.connect();

    }

    @Override
    public void onClick(View v) {

    }


    private void getLibraries() {
        final ArrayList<String> libraryIds = new ArrayList<>();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_GEOFIRE);
        GeoFire geoFire = new GeoFire(ref);
        //Search within 5km, make this changeable at main
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 5.0);


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                libraryIds.add(key);
                System.out.println(String.format("Key %s entered the search area at [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onKeyExited(String key) {
                System.out.println(String.format("Key %s is no longer in the search area", key));
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                System.out.println(String.format("Key %s moved within the search area to [%f,%f]", key, location.latitude, location.longitude));
            }

            @Override
            public void onGeoQueryReady() {
                System.out.println("All initial data has been loaded and events have been fired!");
                if (libraryIds.size() != 0) {
                    displayLibraries(libraryIds);
                }
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                System.err.println("There was an error with this query: " + error);
            }
        });


        mLibraryReference = FirebaseDatabase
                .getInstance()
                .getReference(Constants.FIREBASE_CHILD_LIBRARIES);
    }

    public void displayLibraries(final ArrayList<String> libraryIds) {

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
                GeofireResultsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mListAdapter = new LibraryListAdapter(getApplicationContext(), mLibraries);
                        mLibraryList.setAdapter(mListAdapter);
                        RecyclerView.LayoutManager layoutManager =
                                new LinearLayoutManager(GeofireResultsActivity.this);
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
        int permissionCheck = ContextCompat.checkSelfPermission(GeofireResultsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_DENIED) {
            Log.d(TAG, "permission denied, ask for permission");
            // Here, thisActivity is the current activity
            if (ContextCompat.checkSelfPermission(GeofireResultsActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(GeofireResultsActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION)) {
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(GeofireResultsActivity.this,
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
            Geocoder geocoder = new Geocoder(GeofireResultsActivity.this, Locale.getDefault());
            try {
                List<Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude(), 1);

                if (addresses != null && addresses.size() > 0) {
                    Address returnedAddress = addresses.get(0);
                    getLibraries();
                }
                else {
                    noAddressDialog(GeofireResultsActivity.this);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public void noAddressDialog(Context context) {
        //alert user to none address
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("No location was found");
        builder.setCancelable(true);

        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog noAddressAlert = builder.create();
        noAddressAlert.show();
    }

    private void createAuthProgressDialog() {
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading...");
        mAuthProgressDialog.setMessage("Loading Libraries from Database...");
        mAuthProgressDialog.setCancelable(false);
    }
}
