package com.epicodus.tinylibrarytracker.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danielwulff on 7/20/16.
 */
@Parcel
public class Library {
    int charterNumber;
    int zipCode;
    double latitude;
    double longitude;
    String image;
    private String pushId;

    // empty constructor needed by the Parceler library:
    public Library() {}

    public Library(int charterNumber, int zipCode, double latitude, double longitude, String image) {
        this.charterNumber = charterNumber;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
    }
}

