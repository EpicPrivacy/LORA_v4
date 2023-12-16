package com.example.lorav4.Driver;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.lorav4.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Drivers_track_location extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker currentLocationMarker;
    private List<LatLng> waypoints = new ArrayList<>();
    private Polyline routePolyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_track_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Check and request location permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {
            startLocationUpdates();
        }

        calculateAndDrawRoutes();
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LORA");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map.clear(); // Clear existing markers on the map

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    if (latitude != null && longitude != null && firstName != null && lastName != null
                            && !firstName.equalsIgnoreCase("super") && !lastName.equalsIgnoreCase("admin")) {
                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(firstName + " " + lastName));
                    }
                }

                if (!waypoints.isEmpty()) {
                    LatLng lastLocation = waypoints.get(waypoints.size() - 1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10));
                }

                getLastKnownLocation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void getLastKnownLocation() {
        try {
            // Get the last known location from the LocationManager
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                LatLng currentLocation = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                updateCurrentLocationMarker(currentLocation);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    private void startLocationUpdates() {
        try {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    if (location != null) {
                        // Update the map with the new location using a custom marker icon
                        LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        updateCurrentLocationMarker(currentLocation);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }

                @Override
                public void onProviderEnabled(String provider) {
                }

                @Override
                public void onProviderDisabled(String provider) {
                }
            };

            // Request location updates from the LocationManager
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 0, locationListener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        }
    }
    private void calculateAndDrawRoutes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LORA");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                waypoints.clear(); // Clear existing waypoints

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);

                    // Assuming you have a unique identifier for the current user
                    if (latitude != null && longitude != null && firstName != null && lastName != null
                            && !firstName.equalsIgnoreCase("super") && !lastName.equalsIgnoreCase("admin")
                            && firstName.equalsIgnoreCase("current") && lastName.equalsIgnoreCase("user")) {
                        LatLng currentLocation = new LatLng(latitude, longitude);
                        waypoints.add(currentLocation);
                        break; // Stop after finding the current user's location
                    }
                }

                if (!waypoints.isEmpty()) {
                    // Add other waypoints
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                        Double longitude = dataSnapshot.child("longitude").getValue(Double.class);

                        if (latitude != null && longitude != null) {
                            LatLng location = new LatLng(latitude, longitude);
                            waypoints.add(location);
                        }
                    }
                    // Draw the route
                    drawRoute();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private void updateCurrentLocationMarker(LatLng currentLocation) {
        if (currentLocationMarker != null) {
            // Update the existing marker for the current location
            currentLocationMarker.setPosition(currentLocation);
        } else {
            // Create a new marker for the current location
            currentLocationMarker = map.addMarker(new MarkerOptions().position(currentLocation).title("You are here").icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        }
        if (!waypoints.isEmpty()) {
            drawRoute();
        }
    }

    private void drawRoute() {
        // Draw the polyline route
        if (routePolyline != null) {
            routePolyline.remove(); // Remove existing polyline
        }

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(waypoints)
                .color(Color.BLUE)
                .width(5);
        routePolyline = map.addPolyline(polylineOptions);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        if (locationListener != null) {
            // Remove location updates from the LocationManager
            locationManager.removeUpdates((android.location.LocationListener) locationListener);
        }
    }

}