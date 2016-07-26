package com.epicodus.tinylibrarytracker.models;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import org.parceler.Parcel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Parcel
public class Library {
    int charterNumber;
    int zipCode;
    double latitude;
    double longitude;
    String address;
    String image;
    private String pushId;

    // empty constructor needed by the Parceler library:
    public Library() {}

    public Library(int charterNumber, int zipCode, double latitude, double longitude, String address, String image) {
        this.charterNumber = charterNumber;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.address = address;
    }

    public void setPushId(String id) {
        this.pushId = id;
    }

    public String getPushId() {
        return this.pushId;
    }

    public String getImage() {
        return this.image;
    }

    public String getAddress() { return this.address; }

    public int getZipCode() {
        return this.zipCode;
    }

    public int getCharterNumber() {
        return this.charterNumber;
    }

    public double getLatitude() { return this.latitude; }

    public double getLongitude() { return this.longitude; }
}



