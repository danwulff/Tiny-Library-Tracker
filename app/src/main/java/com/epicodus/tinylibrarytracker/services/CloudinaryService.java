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
import okhttp3.MultipartBody;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;
import se.akerfeldt.okhttp.signpost.SigningInterceptor;

/**
 * Created by Guest on 7/11/16.
 */
public class CloudinaryService {
    public static final String TAG = CloudinaryService.class.getSimpleName();

    public static void uploadPhoto(String imgString, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

        //https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload?file=data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4%2F%2F8%2Fw38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg%3D%3D&folder=tinylibrarypictures&upload_preset=stestv7k

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
//                .add("file", "data%3Aimage%2Fjpeg%3Bbase64%2C" + imgString)
                .addFormDataPart("file", "data%3Aimage%2Fpng%3Bbase64%2CiVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4%2F%2F8%2Fw38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg%3D%3D")
                .addFormDataPart("folder", "tinylibrarypictures")
                .addFormDataPart("upload_preset", "stestv7k")
                .build();

        String url = "https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload";

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Log.d("request url", request.toString());
//        Log.d("request url body", request.url().queryParameterValue(1));
        Call call = client.newCall(request);
        call.enqueue(callback);
    }





    public static String convertBitmapToString(Bitmap img) {
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
        //get square cropped image, http://stackoverflow.com/questions/6908604/android-crop-center-of-bitmap
        int dimension = Math.min(img.getWidth(), img.getHeight());
        img = ThumbnailUtils.extractThumbnail(img, dimension, dimension);
        //to Base64 encoded string
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.JPEG, 100, byteArray);
        byte[] imgArray = byteArray.toByteArray();
        String imgString = Base64.encodeToString(imgArray, Base64.URL_SAFE);

        return imgString;
    }
}
