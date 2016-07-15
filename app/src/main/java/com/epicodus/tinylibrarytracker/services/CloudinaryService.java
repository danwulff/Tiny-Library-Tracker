package com.epicodus.tinylibrarytracker.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by Guest on 7/11/16.
 */
public class CloudinaryService {
    public static final String TAG = CloudinaryService.class.getSimpleName();

    public static void uploadPhoto(String location, Callback callback) {

        Log.d("img location", location);
        // image, convert to Base64 string
        Bitmap img = BitmapFactory.decodeFile(location);
        String imgString = convertBitmapToString(img);

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

//        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload").newBuilder();
//        urlBuilder.addQueryParameter("file", imgString);
//        urlBuilder.addQueryParameter("folder", "tinylibrarypictures");
//        urlBuilder.addQueryParameter("upload_preset", "stestv7k");
//
//        String url = urlBuilder.build().toString();

        String url = "https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload?" +
                "file=" + "data%3Aimage%2Fbitmap%3Bbase64%" + imgString +
                "&folder=tinylibrarypictures" +
                "&upload_preset=stestv7k";


//        Log.d(TAG + ": api url", url);
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }

    private static String convertBitmapToString(Bitmap img) {
        int width = img.getWidth();
        int height = img.getHeight();
        //resize to 3MP if > 3MP (2048 x 1536)
        if( (width > 2048 && height > 1536) || (width > 1536 && height > 2048) ) {
            if (width >= height) {
                int scaledWidth = (int)((double)width / (double)height * 1536.0);
                img = Bitmap.createScaledBitmap(img, scaledWidth, 1536 , false);
            } else {
                int scaledHeight = (int)((double)height / (double)width * 1536.0);
                img = Bitmap.createScaledBitmap(img, 1536, scaledHeight , false);
            }
        }
//        Log.d("scaledWidth", String.valueOf(img.getWidth()));
//        Log.d("scaledHeight", String.valueOf(img.getHeight()));
        //get square cropped image, http://stackoverflow.com/questions/6908604/android-crop-center-of-bitmap
        int dimension = Math.min(img.getWidth(), img.getHeight());
        img = ThumbnailUtils.extractThumbnail(img, dimension, dimension);
        Log.d("width thumbnail", String.valueOf(img.getWidth()));
        Log.d("height thumbnail", String.valueOf(img.getHeight()));
        //to Base64 encoded string
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] imgArray = byteArray.toByteArray();
        String imgString = Base64.encodeToString(imgArray, Base64.URL_SAFE);

        return imgString;
    }
}
