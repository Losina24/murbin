/*
 * Created by Francisco Javier Paños Madrona on 15/11/20 11:29
 * Copyright (c) 2020 . All rights reserved.
 * Last modified 15/11/20 11:29
 */

package com.example.murbin.models;

import java.util.HashMap;
import java.util.Map;

public class Streetlight {

    private com.google.firebase.firestore.GeoPoint location;
    private boolean status;

    /**
     * Constructor Default
     */
    public Streetlight() {
        // Empty
    }

    /**
     * Constructor
     *
     * @param location Geopoint
     * @param status   Streetlight status [On/Off]
     */
    public Streetlight(com.google.firebase.firestore.GeoPoint location, boolean status) {
        this.location = location;
        this.status = status;
    }

    public com.google.firebase.firestore.GeoPoint getLocation() {
        return location;
    }

    public void setLocation(com.google.firebase.firestore.GeoPoint location) {
        this.location = location;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Streetlight{" +
                "location=" + location +
                ", status=" + status +
                '}';
    }

    public Map<String, Object> parseToMap() {
        Map<String, Object> streetlightMap = new HashMap<>();

        streetlightMap.put("status", status);
        streetlightMap.put("location", location);

        return streetlightMap;
    }
}
