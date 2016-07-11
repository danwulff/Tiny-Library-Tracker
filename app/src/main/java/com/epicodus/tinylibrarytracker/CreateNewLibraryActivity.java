package com.epicodus.tinylibrarytracker;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.Bind;
import butterknife.ButterKnife;

public class CreateNewLibraryActivity extends AppCompatActivity {
    @Bind(R.id.backgroundLayout) View mBackgroundLayout;
    @Bind(R.id.selectPhotoButton) Button mSelectPhotoButton;
    @Bind(R.id.placeholderImage) ImageView mPlaceholderImage;

    public static final String TAG = CreateNewLibraryActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_library);
        ButterKnife.bind(this);

        View backgroundimage = mBackgroundLayout;
        Drawable background = backgroundimage.getBackground();
        background.setAlpha(80);

        mSelectPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(view == mSelectPhotoButton){
                    selectImage();
                }
            }
        });;
    }

    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateNewLibraryActivity.this);
        builder.setTitle("Select a Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Log.d(TAG, "take photo chosen");
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 0);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 0) {
                Log.d(TAG, "take photo result");
                takePhotoResult(data);
            }
            else if (requestCode == 1) {
                Log.d(TAG, "choose from library result");
            }
        }
    }

    private void takePhotoResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        mPlaceholderImage.setImageBitmap(thumbnail);
    }

}
