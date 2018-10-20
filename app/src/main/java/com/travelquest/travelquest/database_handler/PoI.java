package com.travelquest.travelquest.database_handler;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

public class PoI implements Serializable{

    private int id_poi;
    private double lat;
    private double lng;
    private String country;
    private String title;
    private String description;
    private String imageLink;

    public PoI(int id_poi, double lat, double lng, String country, String title, String description, String imageLink){
        this.id_poi = id_poi;
        this.title = title;
        this.lat = lat;
        this.lng = lng;
        this.country = country;
        this.description = description;
        this.imageLink = imageLink;
    }

    public LatLng getPosition(){
        return new LatLng(this.lat, this.lng);
    }

    public String getCountry(){
        return this.country;
    }

    public int getID(){
        return this.id_poi;
    }

    public String getTitle(){
        return this.title;
    }

    public String getDescription(){
        return this.description;
    }

    public String getImageLink(){
        return  this.imageLink;
    }

}
