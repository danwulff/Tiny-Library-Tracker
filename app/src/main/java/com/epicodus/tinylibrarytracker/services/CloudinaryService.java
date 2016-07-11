package com.epicodus.tinylibrarytracker.services;

import okhttp3.Callback;
import se.akerfeldt.okhttp.signpost.OkHttpOAuthConsumer;

/**
 * Created by Guest on 7/11/16.
 */
public class CloudinaryService {

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

        //https call needed:
        // https://api.cloudinary.com/v1_1/< cloud name >/<type>/upload&api_key="stuff"&file=DataUri&public_id="name"&signature="hexadecimal shiz"&timestamp=1708237
        // https://api.cloudinary.com/v1_1/tlibrarytracker/image/upload

        //signature = SHA-1 from public_id & timestamp
    }
}
