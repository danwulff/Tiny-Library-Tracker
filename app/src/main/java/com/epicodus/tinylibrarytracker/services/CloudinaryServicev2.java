package com.epicodus.tinylibrarytracker.services;

import android.app.Instrumentation;
import android.net.Uri;
import android.test.InstrumentationTestCase;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.epicodus.tinylibrarytracker.Constants;

/**
 * Created by danwc on 9/1/2016.
 */
public class CloudinaryServicev2{


    public String uploadPhoto(Uri imageFile){
        Map config = new HashMap();
        config.put("cloud_name", Constants.CLOUDINARY_CLOUD_NAME);
        Cloudinary cloudinary = new Cloudinary(config);

        File image = new File(imageFile.getPath());
        Map cloudinaryResult;
        String imageUrl;

        try {
            cloudinaryResult = cloudinary.uploader().unsignedUpload(image , Constants.CLOUDINARY_UPLOAD_PRESET, ObjectUtils.emptyMap());
            imageUrl = cloudinaryResult.get("url").toString();
            return imageUrl;
        }
        catch (IOException e) {
            Log.d("Upload failed: ", e.toString());
            return null;
        }
    }
}