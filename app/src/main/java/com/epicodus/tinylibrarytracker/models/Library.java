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
    double latitude;
    double longitude;
    String image;
    private String pushId;

    // empty constructor needed by the Parceler library:
    public Library() {}

    public Library(String charterNumber, double latitude, double longitude, String image, String pushId) {
        this.charterNumber = charterNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.pushId = pushId;
    }
}

