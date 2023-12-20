package com.example.lorav4.Driver;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.lorav4.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Drivers_track_location extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker currentLocationMarker;
    private List<LatLng> waypoints = new ArrayList<>();
    private Polyline routePolyline;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;



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
            // Permission already granted
        }

        calculateAndDrawRoutes();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            requestLocationUpdates();
            checkLocationSettings();
        }
    }

    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        requestLocationPermission();


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);


        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double currentLatitude = location.getLatitude();
                        double currentLongitude = location.getLongitude();
                        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);


                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                        applyGreedyAlgorithm(currentLocation);
                    }




                });


        map.getUiSettings().setCompassEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String orderStatus = dataSnapshot.child("order_status").getValue(String.class);


                    if (latitude != null && longitude != null && firstName != null && lastName != null
                            && !firstName.equalsIgnoreCase("super") && !lastName.equalsIgnoreCase("admin")) {

                        if ("Ongoing".equalsIgnoreCase(orderStatus)) {
                            LatLng location = new LatLng(latitude, longitude);
                            waypoints.add(location); // Add waypoints for "Ongoing" orders
                            map.addMarker(new MarkerOptions().position(location).title(firstName + " " + lastName));
                        }

                    }
                }

                if (!waypoints.isEmpty()) {
                    LatLng lastLocation = waypoints.get(waypoints.size() - 1);
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation, 10));
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }
    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {

        }
    }
    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


    }
    private void checkLocationSettings() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this)
                        .checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> requestLocationUpdates());

        task.addOnFailureListener(e -> {
            // Handle failure
        });
    }
    private void applyGreedyAlgorithm(LatLng currentLocation) {
        if (waypoints.size() >= 2) {
            List<LatLng> optimizedWaypoints = new ArrayList<>();
            LatLng currentLocationCopy = currentLocation;

            optimizedWaypoints.add(currentLocationCopy);

            while (!waypoints.isEmpty()) {
                int minIndex = 0;
                double minDistance = getDistance(currentLocationCopy, waypoints.get(0));

                for (int i = 1; i < waypoints.size(); i++) {
                    double distance = getDistance(currentLocationCopy, waypoints.get(i));
                    if (distance < minDistance) {
                        minDistance = distance;
                        minIndex = i;
                    }
                }

                currentLocationCopy = waypoints.remove(minIndex);
                optimizedWaypoints.add(currentLocationCopy);
            }

            for (int i = 0; i < optimizedWaypoints.size() - 1; i++) {
                LatLng origin = optimizedWaypoints.get(i);
                LatLng destination = optimizedWaypoints.get(i + 1);
                getDirections(origin, destination);
            }
        }
    }


    private double getDistance(LatLng point1, LatLng point2) {
        return Math.sqrt(Math.pow(point1.latitude - point2.latitude, 2) +
                Math.pow(point1.longitude - point2.longitude, 2));
    }

    private void getDirections(LatLng origin, LatLng destination) {
        String apiKey = "AIzaSyCLZU8pSVc_DisAyPWTpNAYHCVlN9-8mVs";
        String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                "origin=" + origin.latitude + "," + origin.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&mode=driving" +
                "&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> handleDirectionsResponse(response, origin, destination),
                this::handleDirectionsError);

        queue.add(request);
    }


    // Modify handleDirectionsResponse method
    private void handleDirectionsResponse(JSONObject response, LatLng origin, LatLng destination) {
        try {
            if (map != null) {
                JSONArray routes = response.getJSONArray("routes");

                if (routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String points = overviewPolyline.getString("points");
                    List<LatLng> decodedPath = PolyUtil.decode(points);

                    // Draw a new polyline for the route with a different color
                    PolylineOptions polylineOptions = new PolylineOptions().width(10).color(getRandomColor());
                    polylineOptions.addAll(decodedPath);
                    map.addPolyline(polylineOptions);
                    if (routePolyline != null) {
                        routePolyline.remove();
                    }

                }
            } else {
                Log.e("Map", "Map object is null or not ready");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private int getRandomColor() {
        // Generate a random color for each route
        Random random = new Random();
        return Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }



    private void handleDirectionsError(VolleyError error) {
        error.printStackTrace();
    }


    private void calculateAndDrawRoutes() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("orders");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                waypoints.clear(); // Clear existing waypoints

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                    Double longitude = dataSnapshot.child("longitude").getValue(Double.class);
                    String firstName = dataSnapshot.child("firstName").getValue(String.class);
                    String lastName = dataSnapshot.child("lastName").getValue(String.class);
                    String orderStatus = dataSnapshot.child("order_status").getValue(String.class);


                    // Assuming you have a unique identifier for the current user
                    if (latitude != null && longitude != null && firstName != null && lastName != null
                            && !firstName.equalsIgnoreCase("super") && !lastName.equalsIgnoreCase("admin")) {

                        if ("Ongoing".equalsIgnoreCase(orderStatus)) {
                            LatLng location = new LatLng(latitude, longitude);
                            waypoints.add(location); // Add waypoints for "Ongoing" orders
                            map.addMarker(new MarkerOptions().position(location).title(firstName + " " + lastName));
                        }

                    } else {
                        // Add other waypoints
                        if (latitude != null && longitude != null) {
                            LatLng location = new LatLng(latitude, longitude);
                            waypoints.add(location);
                        }
                    }
                }

                if (!waypoints.isEmpty() && waypoints.size() > 1) {
                    // Draw the route only if there are at least two waypoints
                    drawRoute(waypoints);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }




    private void drawRoute(List<LatLng> waypoints) {
        // Clear existing polyline
        if (routePolyline != null) {
            routePolyline.remove();
        }

        // Draw the new polyline route
        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(waypoints)
                .width(10);
        routePolyline = map.addPolyline(polylineOptions);
    }


    private double calculateTotalDistance(List<LatLng> waypoints) {
        double totalDistance = 0.0;
        for (int i = 0; i < waypoints.size() - 1; i++) {
            totalDistance += getDistance(waypoints.get(i), waypoints.get(i + 1));
        }
        return totalDistance;
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