package com.example.lorav4;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        // Retrieve the passed current location
        if (getIntent().hasExtra("currentLocation")) {
            LatLng currentLocation = getIntent().getParcelableExtra("currentLocation");
            // Use the currentLocation as needed
            // For example, you can add a marker to the map:
            if (map != null) {
                map.addMarker(new MarkerOptions().position(currentLocation).title("Current Location"));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
            }
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // Use the GoogleMap object as needed
        map = googleMap;
    }
}
