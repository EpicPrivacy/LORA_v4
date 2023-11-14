package com.example.lorav4;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.maps.GeoApiContext;

import java.util.Arrays;


public class Update_location extends AppCompatActivity implements OnMapReadyCallback {

    GoogleMap map;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LatLng destinationLatLng;
    Location lastLocation;
    private boolean isCameraMoving = false;

    private SearchView searchView;
    private PlacesClient placesClient;
    private GeoApiContext geoApiContext;


    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_location);


        requestLocationPermission();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        // Update the map with the current location
                        updateMapWithCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                }
            }
        };

        // Request location updates
        requestLocationUpdates();

        Places.initialize(getApplicationContext(), getString(R.string.maps));
        placesClient = Places.createClient(this);

        // Initialize Directions API
        geoApiContext = new GeoApiContext.Builder()
                .apiKey(getString(R.string.maps))
                .build();

        // Get reference to the SearchView
        searchView = findViewById(R.id.searchView);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform the search when the user submits the query
                setupSearchBar();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle any changes to the search query text (optional)
                return false;
            }
        });

        // Add the onClose listener to prevent the app from closing when the close button is clicked
        searchView.setOnCloseListener(() -> {
            // Do nothing or handle the close event as needed
            return false;
        });
    }

    private void requestLocationPermission() {
    }

    private void setupSearchBar() {
        // Initialize AutocompleteSupportFragment
        AutocompleteSupportFragment autocompleteFragment =
                (AutocompleteSupportFragment) getSupportFragmentManager().findFragmentById(R.id.maps);

        // Specify the types of place data to return
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG));

        // Set up place selection listener
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                // Place has been selected. Add a marker to the map.
                LatLng selectedPlaceLatLng = place.getLatLng();

                // Update the destinationLatLng
                destinationLatLng = selectedPlaceLatLng;

                map.addMarker(new MarkerOptions().position(selectedPlaceLatLng).title(place.getName()));
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedPlaceLatLng, 12));
            }

            @Override
            public void onError(@NonNull com.google.android.gms.common.api.Status status) {
                // Handle the error
                Log.e("PlaceSelection", "Error: " + status);
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location updates
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                map.setMyLocationEnabled(true);
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable functionality)
            }
        }
    }


    private void requestLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // Add a marker to the default location
        LatLng defaultLocation = new LatLng(14.181065806839685, 121.22518595716619); // Replace with your default location
        map.addMarker(new MarkerOptions().position(defaultLocation).title("Default Marker"));
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 12));

        // Enable My Location button and track user's location
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        map.setMyLocationEnabled(true);

        // Request location updates
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    double currentLatitude = location.getLatitude();
                    double currentLongitude = location.getLongitude();

                    // Use the current location coordinates as needed
                    LatLng currentLocation = new LatLng(currentLatitude, currentLongitude);

                    // You can now use the 'currentLocation' LatLng object for your purposes
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
    }


    private void updateMapWithCurrentLocation(LatLng currentLatLng) {
        if (map != null) {
            // Clear previous markers
            map.clear();

            // Add the destination marker
            if (destinationLatLng != null) {
                map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
            }

            // Add the current location marker
            map.addMarker(new MarkerOptions().position(currentLatLng).title("Current Location"));

            // Move the camera to the current location only if the user hasn't manually moved the camera
            if (!isCameraMoving) {
                // Check if a destination is set
                if (destinationLatLng != null) {
                    // Calculate bounds for both current location and destination
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    builder.include(currentLatLng);
                    builder.include(destinationLatLng);
                    LatLngBounds bounds = builder.build();

                    // Move the camera to include both current location and destination
                    map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 16));
                } else {
                    // Move the camera to the current location if no destination is set
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 16.0f));
                }
            }

            // Reset the flag after moving the camera
            isCameraMoving = false;
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Stop location updates when the activity is destroyed
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}