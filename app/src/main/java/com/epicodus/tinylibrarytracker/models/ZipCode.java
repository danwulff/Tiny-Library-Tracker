package com.epicodus.tinylibrarytracker.models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by danwc on 7/24/2016.
 */
public class ZipCode {
    int zipCode;
    List<String> libraries = new ArrayList<>(); //list of library firebase Ids (not library objects)

    public ZipCode(){}

    public ZipCode(int zipCode){
        this.zipCode = zipCode;
    }

    public void addLibrary(String libraryId){
        libraries.add(libraryId);
    }

    public List<String> getLibraries(){
        return libraries;
    }
}
