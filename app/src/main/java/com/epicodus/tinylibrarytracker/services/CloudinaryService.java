package com.epicodus.tinylibrarytracker.services;

import android.util.Log;

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
        //from yelp api call
//        OkHttpOAuthConsumer consumer = new OkHttpOAuthConsumer(Constants.YELP_CONSUMER_KEY, Constants.YELP_CONSUMER_SECRET);
//        consumer.setTokenWithSecret(Constants.YELP_TOKEN, Constants.YELP_TOKEN_SECRET);
//
//        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(new SigningInterceptor(consumer)).build();
//
//        HttpUrl.Builder urlBuilder = HttpUrl.parse(Constants.YELP_BASE_URL).newBuilder();
//        urlBuilder.addQueryParameter(Constants.YELP_LOCATION_QUERY_PARAMETER, location);
//        Log.d("url object", "" + urlBuilder.build());
//        String url = urlBuilder.build().toString();
//
//        Request request = new Request.Builder().url(url).build();
//
//        Call call = client.newCall(request);
//        call.enqueue(callback);

        //https call needed for authenticated requests:
        // https://api.cloudinary.com/v1_1/< cloud name >/<type>/upload&api_key="stuff"&file=DataUri&public_id="name"&signature="hexadecimal shiz"&timestamp=1708237
        // https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload

        //http call needed for unauthenticated requests:
        //https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload

//        file=https://pbs.twimg.com/profile_images/378800000274368432/a76142d1ae2d569365384899e1e6b9d4.jpeg
//        upload_preset=stestv7k

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload").newBuilder();
        urlBuilder.addQueryParameter("file", "https://pbs.twimg.com/profile_images/378800000274368432/a76142d1ae2d569365384899e1e6b9d4.jpeg");
        urlBuilder.addQueryParameter("folder", "tinylibrarypictures");
        urlBuilder.addQueryParameter("upload_preset", "stestv7k");

        String url = urlBuilder.build().toString();
        Log.d(TAG + "api url", url);
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
