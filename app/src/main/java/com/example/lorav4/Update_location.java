package com.example.lorav4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


// Import statements...

public class Update_location extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private SearchView searchView;
    private List<LatLng> waypoints = new ArrayList<>();
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private FusedLocationProviderClient fusedLocationClient;

    private Polyline currentPolyline;

    private Button Clear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);

        searchView = findViewById(R.id.searchView);

        Clear = findViewById(R.id.Clear);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);

        Clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearPolylines();
                map.clear();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String locationQuery = searchView.getQuery().toString();
                List<Address> addressList = null;

                if (locationQuery != null) {
                    Geocoder geocoder = new Geocoder(Update_location.this);

                    try {
                        addressList = geocoder.getFromLocationName(locationQuery, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                        // Clear existing markers on the map
                        map.clear();

                        // Add a new marker for the searched location
                        map.addMarker(new MarkerOptions().position(latLng).title(locationQuery));
                        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));

                        // Draw route from current location to the searched location
                        if (ActivityCompat.checkSelfPermission(Update_location.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            fusedLocationClient.getLastLocation()
                                    .addOnSuccessListener(Update_location.this, location -> {
                                        if (location != null) {
                                            LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                            drawRoute(currentLocation, latLng);
                                            waypoints.add(currentLocation);
                                            waypoints.add(latLng);
                                            applyGreedyAlgorithm();
                                        }
                                    });
                        }
                    }
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

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

    private void clearPolylines() {
        if (currentPolyline != null) {
            currentPolyline.remove();
            currentPolyline = null;
        }
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
    private void drawRoute(LatLng currentLocation, LatLng destination) {
        // Clear existing polylines on the map
        clearPolylines();

        // Draw a new polyline
        PolylineOptions line = new PolylineOptions()
                .add(currentLocation, destination)
                .width(5)
                .color(Color.RED);
        currentPolyline = map.addPolyline(line);
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

    private void handleDirectionsResponse(JSONObject response) {
        try {
            // Clear existing polylines on the map
            clearPolylines();

            JSONArray routes = response.getJSONArray("routes");
            for (int i = 0; i < routes.length(); i++) {
                JSONObject route = routes.getJSONObject(i);
                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                String points = overviewPolyline.getString("points");
                List<LatLng> decodedPath = PolyUtil.decode(points);

                // Draw a new polyline
                PolylineOptions line = new PolylineOptions().width(5).color(Color.RED);
                line.addAll(decodedPath);
                currentPolyline = map.addPolyline(line);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void handleDirectionsError(VolleyError error) {
        error.printStackTrace();
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

    private void requestLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    // Do something with the latitude and longitude
                }
            }
        }, null);
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        } else {
            map.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                map.setMyLocationEnabled(true);
            } else {
                // Handle permission denied
            }
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        requestLocationPermission();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);
                    // Use the 'currentLocation' LatLng object as needed
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
            });
        }

        map.getUiSettings().setCompassEnabled(true);
    }
}
