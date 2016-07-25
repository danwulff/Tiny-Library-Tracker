package com.epicodus.tinylibrarytracker.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danwc on 7/24/2016.
 */
public class ZipCode {
    int zipCode;
    List<String> libraries = new ArrayList<>();

    public ZipCode(){}

    public ZipCode(int zipCode){
        this.zipCode = zipCode;
    }

    public void addLibrary(String library){
        libraries.add(library);
    }

    public List<String> getLibraries(){
        return libraries;
    }
}
