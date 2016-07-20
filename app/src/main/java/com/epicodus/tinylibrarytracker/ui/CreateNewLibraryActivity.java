package com.epicodus.tinylibrarytracker.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.epicodus.tinylibrarytracker.R;
import com.epicodus.tinylibrarytracker.services.CloudinaryService;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CreateNewLibraryActivity extends AppCompatActivity implements View.OnClickListener {
    @Bind(R.id.backgroundLayout) View mBackgroundLayout;
    @Bind(R.id.selectPhotoButton) Button mSelectPhotoButton;
    @Bind(R.id.newLibraryButton) Button mNewLibraryButton;
    @Bind(R.id.placeholderImage) ImageView mPlaceholderImage;

    public static final String TAG = CreateNewLibraryActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_library);
        ButterKnife.bind(this);

        //set background image
        View backgroundimage = mBackgroundLayout;
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(80);

        mSelectPhotoButton.setOnClickListener(this);
        mNewLibraryButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == mSelectPhotoButton){
            Log.d(TAG, "clicked choose a photo");
            selectImage();
        } else if (view == mNewLibraryButton) {
            Log.d(TAG, "clicked submit");
            //check to make sure all fields having proper input
            if(newPhotoUri != null) {
                Log.d(TAG + " photo URI", newPhotoUri.toString());
                createLibrary();
            }
            else {
                //display warning, do nothing
                Log.d(TAG, "no photoUri");
            }
        }
    }


    //----------------------------------------------------------------------------------------------
    //selectImage
    //-----------
    static final int MY_PERMISSIONS_REQUEST_USE_EXTERNAL = 100;

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewLibraryActivity.this);
        builder.setTitle("Select a Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Log.d(TAG, "take photo chosen");
                    int permissionCheck = ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    if(permissionCheck == PackageManager.PERMISSION_DENIED) {
                        Log.d(TAG, "permission denied, ask for permission");
                        // Here, thisActivity is the current activity
                        if (ContextCompat.checkSelfPermission(CreateNewLibraryActivity.this,
                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {

                            // Should we show an explanation?
                            if (ActivityCompat.shouldShowRequestPermissionRationale(CreateNewLibraryActivity.this,
                                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                                // Show an expanation to the user *asynchronously* -- don't block
                                // this thread waiting for the user's response! After the user
                                // sees the explanation, try again to request the permission.

                            } else {
                                // No explanation needed, we can request the permission.
                                ActivityCompat.requestPermissions(CreateNewLibraryActivity.this,
                                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        MY_PERMISSIONS_REQUEST_USE_EXTERNAL);

                                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                                // app-defined int constant. The callback method gets the
                                // result of the request.
                            }
                        }
                    } else if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    Log.d(TAG, "choose from library chosen");
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_USE_EXTERNAL: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
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
    String photoFileName = "tinyLibraryTest.jpg";   //will overwrite previous picture in gallery at the moment, need to create dynamic name
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
        Log.d(TAG, "createLibrary()");

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
                } catch (Exception e) {
                    e.printStackTrace();;
                }

                Log.d(TAG + "onResponse ", jsonData);
            }
        });


        //eventually publish library object to firebase with photo URL from API call,
        //instead, as a placeholder, call new activity and pass url, to see url loaded from internet url
    }
}
