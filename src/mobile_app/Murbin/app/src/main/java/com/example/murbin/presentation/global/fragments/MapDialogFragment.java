/*
 * Created by Francisco Javier Paños Madrona on 17/01/21 14:21
 * Copyright (c) 2021 . All rights reserved.
 * Last modified 17/01/21 14:21
 */

package com.example.murbin.presentation.global.fragments;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.murbin.App;
import com.example.murbin.R;
import com.example.murbin.models.Area;
import com.example.murbin.models.GeoPoint;
import com.example.murbin.presentation.zone.administrator.AdministratorSubzoneCreateActivity;
import com.example.murbin.presentation.zone.administrator.AdministratorSubzoneEditActivity;
import com.example.murbin.presentation.zone.technician.TechnicianStreetlightCreateActivity;
import com.example.murbin.presentation.zone.technician.TechnicianStreetlightEditActivity;
import com.example.murbin.uses_cases.UsesCasesZoneAdministrator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * DialogFragment
 * https://developer.android.com/reference/android/app/DialogFragment.html#BasicDialog
 */
public class MapDialogFragment extends androidx.fragment.app.DialogFragment
        implements OnMapReadyCallback, GoogleMap.OnPolygonClickListener, View.OnClickListener {

    private final List<GeoPoint> listGeoPoints;
    private final String originActivity;
    private final String action;
    private SupportMapFragment mMapView;
    private Button m_btn_validate, m_btn_cancel;
    private GoogleMap mMap;
    private Area areaSubzone;
    private GeoPoint location;
    private Polygon polygon;

    /**
     * Constructor
     */
    public MapDialogFragment(String action, String originActivity) {
        listGeoPoints = new ArrayList<>();
        this.action = action;
        this.originActivity = originActivity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.administrator_dialog_fragment_map, container, false);

        mMapView = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.administrator_dialog_fragment_map_mapview);
        mMapView.getMapAsync(this);

        m_btn_validate = view.findViewById(R.id.administrator_dialog_fragment_map_btn_validate);
        m_btn_validate.setOnClickListener(this);
        m_btn_cancel = view.findViewById(R.id.administrator_dialog_fragment_map_btn_cancel);
        m_btn_cancel.setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        if (mMapView != null)
            getChildFragmentManager().beginTransaction()
                    .remove(mMapView).commitAllowingStateLoss();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        if (mMapView != null) {
            mMapView.onDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mMapView != null) {
            mMapView.onLowMemory();
        }
    }

    @Override
    public void onPolygonClick(Polygon polygon) {
//        Log.d(App.DEFAULT_TAG, "Pulsada Zona: " + polygon.getTag().toString());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        GeoPoint geoPoint;
        if (App.getInstance().hasPermissions(getContext(), App.ARRAY_PERMISSIONS_LOAD_MAP)) {
            UsesCasesZoneAdministrator icza = new UsesCasesZoneAdministrator();
            geoPoint = icza.getCurrentLocation(getContext());
        } else {
            geoPoint = new GeoPoint();
        }

        //Log.d(App.DEFAULT_TAG, "geoPoint:" + geoPoint.toString());

        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = mMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_google_map)
            );
            if (!success) {
//                Log.d(App.DEFAULT_TAG, "Error al analizar el estilo.");
            }
        } catch (Resources.NotFoundException e) {
//            Log.d(App.DEFAULT_TAG, "No encuentro el estilo. Error: ", e);
        }

        LatLng centerPoint = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centerPoint, 12));

        mMap.setOnMapClickListener(
                latLng -> {
//                    Log.d(App.DEFAULT_TAG, "Latitude:" + latLng.latitude + " - Longitude: " + latLng.longitude);
                    switch (action) {
                        case "polygon": {
                            listGeoPoints.add(new GeoPoint(latLng.latitude, latLng.longitude));
//                    Log.d(App.DEFAULT_TAG, "List:" + listGeoPoints.toString());
                            areaSubzone = new Area(listGeoPoints);
                            polygon = null;
                            polygon = googleMap.addPolygon(new PolygonOptions()
                                    .addAll(areaSubzone.convertToLatLngList()));
                            polygon.setTag("subzone");
                            mMap.addMarker(new MarkerOptions().position(latLng));
                            break;
                        }
                        case "location": {
                            location = new GeoPoint(latLng.latitude, latLng.longitude);
                            mMap.addMarker(new MarkerOptions().position(latLng));
                            break;
                        }
                    }
                }
        );
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.administrator_dialog_fragment_map_btn_validate) {
//            Log.d(App.DEFAULT_TAG, "Pulsado:" + "administrator_dialog_fragment_map_btn_validate");
//            Log.d(App.DEFAULT_TAG, "List end:" + listGeoPoints.toString());
//            Log.d(App.DEFAULT_TAG, "Area end:" + areaSubzone.toString());
            switch (action) {
                case "polygon": {
                    callSetAreaSubzoneCorrectly();
                    break;
                }
                case "location": {
                    callSetLocationStreetlightCorrectly();
                    break;
                }
            }
            dismiss();
        }
        if (id == R.id.administrator_dialog_fragment_map_btn_cancel) {
//            Log.d(App.DEFAULT_TAG, "Pulsado:" + "administrator_dialog_fragment_map_btn_cancel");
//            listGeoPoints.clear();
//            areaSubzone = new Area();
            switch (action) {
                case "polygon": {
                    callSetAreaSubzoneCorrectly();
                    break;
                }
                case "location": {
                    callSetLocationStreetlightCorrectly();
                    break;
                }
            }
            dismiss();
        }
    }

    /**
     *
     */
    private void callSetAreaSubzoneCorrectly() {
        switch (this.originActivity) {
            case "AdministratorSubzoneCreateActivity": {
//                Log.d(App.DEFAULT_TAG, String.valueOf(areaSubzone.getArea().size()));
                ((AdministratorSubzoneCreateActivity) getActivity()).setAreaSubzone(areaSubzone);
                break;
            }
            case "AdministratorSubzoneEditActivity": {
//                Log.d(App.DEFAULT_TAG, String.valueOf(areaSubzone.getArea().size()));
                ((AdministratorSubzoneEditActivity) getActivity()).setAreaSubzone(areaSubzone);
                break;
            }
        }
    }

    /**
     *
     */
    private void callSetLocationStreetlightCorrectly() {
        switch (this.originActivity) {
            case "TechnicianStreetlightCreateActivity": {
                ((TechnicianStreetlightCreateActivity) getActivity()).setLocationStreetlight(new com.google.firebase.firestore.GeoPoint(location.getLatitude(), location.getLongitude()));
                break;
            }
            case "TechnicianStreetlightEditActivity": {
                ((TechnicianStreetlightEditActivity) getActivity()).setLocationStreetlight(new com.google.firebase.firestore.GeoPoint(location.getLatitude(), location.getLongitude()));
                break;
            }
        }
    }
}