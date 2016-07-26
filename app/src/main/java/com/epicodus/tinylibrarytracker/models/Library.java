package com.epicodus.tinylibrarytracker.models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

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

    public Library(int charterNumber, int zipCode, double latitude, double longitude, String image) {
        this.charterNumber = charterNumber;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
        this.image = image;
        this.address = getAddressFromCoordinates(this.latitude, this.longitude);
    }

    public void setPushId(String id) {
        this.pushId = id;
    }

    public String getPushId() {
        return this.pushId;
    }


    private String getAddressFromCoordinates(double latitude, double longitude) {
        /*Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);

            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder();
                for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("");
                }
                et_lugar.setText(strReturnedAddress.toString());
            }
            else {
                et_lugar.setText("No Address returned!");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            et_lugar.setText("Canont get Address!");
        } */

        return "address tbd";
    }

    public String getImageUrl() {
        return this.image;
    }

    public String getAddress() {
        return this.address;
    }

    public int getZipCode() {
        return this.zipCode;
    }

    public int getCharterNumber() {
        return this.charterNumber;
    }
}



