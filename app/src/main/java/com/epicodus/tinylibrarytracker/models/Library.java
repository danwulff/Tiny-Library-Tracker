package com.epicodus.tinylibrarytracker.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielwulff on 7/20/16.
 */
@Parcel
public class Library {
    String charterNumber;
    int zipCode;
    double latitude;
    double longitude;
    List<String> images = new ArrayList<>();
    private String pushId;

    // empty constructor needed by the Parceler library:
    public Library() {}

    public Library(String charterNumber, int zipCode, double latitude, double longitude, List<String> images, String pushId) {
        this.charterNumber = charterNumber;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.images = images;
        this.pushId = pushId;
    }
}

