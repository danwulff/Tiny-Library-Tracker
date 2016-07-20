package com.epicodus.tinylibrarytracker.services;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.util.Log;
import android.util.Base64;

import org.apache.http.protocol.HTTP;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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
                .connectTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();

//        String image = "data:image/png;base64," + imgString;
        String image = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAYAAACNbyblAAAAHElEQVQI12P4//8/w38GIAXDIBKE0DHxgljNBAAO9TXL0Y4OHwAAAABJRU5ErkJggg==";
        String urlEncoded;
        try {
            urlEncoded = URLEncoder.encode(image, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new AssertionError("UTF-8 is unknown");
            // or 'throw new AssertionError("Impossible things are happening today. " +
            //                              "Consider buying a lottery ticket!!");'
        }
        Log.d("image string", image);
        Log.d("image string encoded", urlEncoded);

        RequestBody requestBody = new FormBody.Builder()
                .addEncoded("file", image)
//                .add("file", urlEncoded)
                .add("folder", "tinylibrarypictures")
                .add("upload_preset", "stestv7k")
                .build();

       String url = "https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload";

        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        Log.d("request url", request.toString());
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
        img = ThumbnailUtils.extractThumbnail(img, 10, 10);
        //to Base64 encoded string
        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        img.compress(Bitmap.CompressFormat.PNG, 100, byteArray);
        byte[] imgArray = byteArray.toByteArray();
        String imgString = Base64.encodeToString(imgArray, Base64.DEFAULT);

        return imgString;
    }
}
