package com.example.lorav4.Driver;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_track_location);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        calculateAndDrawRoutes();
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        // Replace "your_node" with the actual node where your location data is stored in Firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LORA");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                map.clear(); // Clear existing markers on the map

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    // Assuming your data structure has "latitude" and "longitude" fields
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String mobileNumber = firstName + " " + lastName;

                    if (latitude != null && longitude != null && firstName != null && lastName != null
                            && !firstName.equalsIgnoreCase("super") && !lastName.equalsIgnoreCase("admin")) {
                        LatLng location = new LatLng(latitude, longitude);
                        map.addMarker(new MarkerOptions().position(location).title(firstName + " " + lastName));

                        // Optionally, move the camera to focus on the markers
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 10));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
    private void calculateAndDrawRoutes() {
        // Replace this with your list of waypoints
        List<LatLng> waypoints = new ArrayList<>();

        // Get the last known location from the Firebase data
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LORA");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
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

                    // Apply the greedy algorithm to optimize the order of waypoints
                    List<LatLng> optimizedWaypoints = applyGreedyAlgorithm(waypoints);

                    // Draw the route using polylines
                    getDirections(optimizedWaypoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }

    private float getDistance(LatLng point1, LatLng point2) {
        Location location1 = new Location("point1");
        location1.setLatitude(point1.latitude);
        location1.setLongitude(point1.longitude);

        Location location2 = new Location("point2");
        location2.setLatitude(point2.latitude);
        location2.setLongitude(point2.longitude);

        return location1.distanceTo(location2);
    }


    private List<LatLng> applyGreedyAlgorithm(List<LatLng> waypoints) {
        if (waypoints.size() >= 2) {
            List<LatLng> optimizedWaypoints = new ArrayList<>();
            LatLng currentLocation = waypoints.get(0);
            optimizedWaypoints.add(currentLocation);

            while (waypoints.size() > 1) {
                int minIndex = 1;
                float minDistance = Float.MAX_VALUE; // Change to float

                for (int i = 1; i < waypoints.size(); i++) {
                    float distance = getDistance(currentLocation, waypoints.get(i));
                    if (distance < minDistance) {
                        minDistance = distance;
                        minIndex = i;
                    }
                }

                currentLocation = waypoints.remove(minIndex);
                optimizedWaypoints.add(currentLocation);
            }
            return optimizedWaypoints;
        } else {
            return waypoints;
        }
    }

    private void getDirections(List<LatLng> waypoints) {
        // Draw polylines for different paths
        for (int i = 0; i < waypoints.size() - 1; i++) {
            LatLng origin = waypoints.get(i);
            LatLng destination = waypoints.get(i + 1);

            // Draw a polyline between each pair of waypoints
            PolylineOptions polylineOptions = new PolylineOptions()
                    .add(origin, destination)
                    .width(5)
                    .color(Color.BLUE);
            map.addPolyline(polylineOptions);

            // Optionally, you can add markers at the origin and destination
            map.addMarker(new MarkerOptions().position(origin).title("Start"));
            map.addMarker(new MarkerOptions().position(destination).title("End"));
        }
    }




    @Override
    protected void onPause() {
        super.onPause();
        // This method is called when the activity is no longer in the foreground.
        // You might want to stop ongoing processes or resources here.
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This method is called when the activity is being destroyed.
        // Release resources, unregister listeners, or perform cleanup tasks here.
    }
}