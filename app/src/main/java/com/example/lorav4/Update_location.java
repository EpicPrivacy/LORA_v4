package com.example.lorav4;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Update_location extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private double currentLatitude;
    private double currentLongitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);

        // Retrieve current location data from the intent
        Intent intent = getIntent();
        currentLatitude = intent.getDoubleExtra("currentLatitude", 0.0);
        currentLongitude = intent.getDoubleExtra("currentLongitude", 0.0);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Move the camera to the current location and add a marker
        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

        // Add a marker at the current location
        map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
    }
}
