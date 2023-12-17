package com.example.lorav4.Driver;

import android.Manifest;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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
import java.util.Locale;

public class Drivers_track_location extends AppCompatActivity implements OnMapReadyCallback{

    private GoogleMap map;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker currentLocationMarker;
    private List<LatLng> waypoints = new ArrayList<>();
    private Polyline routePolyline;
    public static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng currentLocation,destination;

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
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        double currentLatitude = location.getLatitude();
                        double currentLongitude = location.getLongitude();
                        LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);


                        // Add a new circle for the current location with a larger radius
                        float markerSize = getResources().getDimension(R.dimen.marker_size);
                        currentLocationMarker = map.addMarker(new MarkerOptions()
                                .position(currentLocation)
                                .title("You are here")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker))
                                .anchor(0.5f, 0.5f) // Center the marker on the location
                                .flat(true)); // Keep the marker flat on the map
                        currentLocationMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.gps_marker));


                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));

                        applyGreedyAlgorithm();
                    }
                });


        map.getUiSettings().setCompassEnabled(true);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("LORA");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

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
    private void applyGreedyAlgorithm() {
        if (waypoints.size() >= 2) {
            List<LatLng> optimizedWaypoints = new ArrayList<>();
            LatLng currentLocation = waypoints.get(0);
            optimizedWaypoints.add(currentLocation);

            while (waypoints.size() > 1) {
                int minIndex = 1;
                double minDistance = Double.MAX_VALUE;

                for (int i = 1; i < waypoints.size(); i++) {
                    double distance = getDistance(currentLocation, waypoints.get(i));
                    if (distance < minDistance) {
                        minDistance = distance;
                        minIndex = i;
                    }
                }

                currentLocation = waypoints.remove(minIndex);
                optimizedWaypoints.add(currentLocation);
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
                "&key=" + apiKey;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                this::handleDirectionsResponse,
                this::handleDirectionsError);

        queue.add(request);
    }
    // Add this array as a class variable
    private final int[] polylineColors = {Color.YELLOW, Color.RED, Color.GREEN, Color.BLUE, Color.MAGENTA};

    private int currentRouteColorIndex = 0; // Add this as a class variable

    // Modify handleDirectionsResponse method
    private void handleDirectionsResponse(JSONObject response) {
        try {
            if (map != null) {
                JSONArray routes = response.getJSONArray("routes");

                if (currentRouteColorIndex < polylineColors.length && routes.length() > 0) {
                    JSONObject route = routes.getJSONObject(0);
                    JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                    String points = overviewPolyline.getString("points");
                    List<LatLng> decodedPath = PolyUtil.decode(points);

                    // Draw a new polyline for the route with a different color
                    PolylineOptions polylineOptions = new PolylineOptions().width(7).color(polylineColors[currentRouteColorIndex]);
                    polylineOptions.addAll(decodedPath);
                    map.addPolyline(polylineOptions);

                    currentRouteColorIndex++;
                }
            } else {
                Log.e("Map", "Map object is null or not ready");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }





    private void handleDirectionsError(VolleyError error) {
        error.printStackTrace();
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

                    } else {

                    // Add other waypoints
                    if (latitude != null && longitude != null) {
                        destination = new LatLng(latitude, longitude);
                        waypoints.add(destination);
                    }
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
                    if (currentLocation != null && destination != null) {
                        // Draw the route
                        drawRoute(currentLocation, destination);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("Firebase", "Failed to read value.", error.toException());
            }
        });
    }


    private void drawRoute(LatLng currentLocation, LatLng destination) {
        // Draw the polyline route

        PolylineOptions polylineOptions = new PolylineOptions()
                .addAll(waypoints)
                .color(Color.BLUE)
                .width(5);
        routePolyline = map.addPolyline(polylineOptions);


        double distance = Double.parseDouble(calculateDistance(currentLocation, destination));
        String formattedDistance = String.format(Locale.getDefault(), "%.2f km", distance);

    }
    private String calculateDistance(LatLng start, LatLng end) {
        float[] results = new float[1];
        Location.distanceBetween(start.latitude, start.longitude, end.latitude, end.longitude, results);

        // Convert meters to kilometers and format to two decimal places
        double distanceInMeters = results[0];
        double distanceInKm = distanceInMeters / 1000.0;
        return String.format(Locale.getDefault(), "%.2f", distanceInKm);
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