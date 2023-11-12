package com.example.lorav4;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;

import java.util.Arrays;
import java.util.List;


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
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Handle any changes to the search query text (optional)
                return false;
            }
        });
    }

    private void performSearch(String query) {
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setTypeFilter(TypeFilter.ADDRESS)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request)
                .addOnSuccessListener((response) -> {
                    if (!response.getAutocompletePredictions().isEmpty()) {
                        AutocompletePrediction autocompletePrediction = response.getAutocompletePredictions().get(0);
                        String placeId = autocompletePrediction.getPlaceId();

                        // Fetch place details using the place ID
                        fetchPlaceDetails(placeId);
                    } else {
                        Log.e("Search", "No predictions found");
                    }
                })
                .addOnFailureListener((exception) -> {
                    Log.e("Search", "Error getting autocomplete predictions: " + exception.getMessage());
                });
    }
    private void fetchPlaceDetails(String placeId) {
        // Specify the fields to be returned
        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME);

        // Create a FetchPlaceRequest
        FetchPlaceRequest request = FetchPlaceRequest.builder(placeId, placeFields).build();

        // Use PlacesClient to fetch place details
        placesClient.fetchPlace(request)
                .addOnSuccessListener((response) -> {
                    Place place = response.getPlace();
                    LatLng newDestinationLatLng = place.getLatLng();

                    // Update the destinationLatLng
                    destinationLatLng = newDestinationLatLng;

                    // Now you can use destinationLatLng for further processing
                    updateMapWithDestination(newDestinationLatLng);
                    calculateAndDisplayRoute(newDestinationLatLng);
                })
                .addOnFailureListener((exception) -> {
                    Log.e("FetchPlace", "Error fetching place details: " + exception.getMessage());
                });
    }
    private void calculateAndDisplayRoute(LatLng destinationLatLng) {
        // Use Directions API to calculate and display the route
        DirectionsApiRequest request = DirectionsApi.getDirections(geoApiContext,
                        lastLocation.getLatitude() + "," + lastLocation.getLongitude(),
                        destinationLatLng.latitude + "," + destinationLatLng.longitude)
                .mode(TravelMode.DRIVING);

        try {
            DirectionsResult result = request.await();
            if (result != null && result.routes != null && result.routes.length > 0) {
                PolylineOptions polylineOptions = new PolylineOptions();
                for (com.google.maps.model.LatLng point : result.routes[0].overviewPolyline.decodePath()) {
                    polylineOptions.add(new LatLng(point.lat, point.lng));

                }
                map.addPolyline(polylineOptions);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

// ...

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with location updates
                requestLocationUpdates();
            } else {
                // Permission denied, handle accordingly (e.g., show a message, disable functionality)
            }
        }
    }


    private void requestLocationUpdates() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000); // 5 seconds

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        float zoomLevel = 16.0f;
        // 14.180989, 121.225210
        destinationLatLng = new LatLng(14.181092519228185, 121.22517763491443);

        // Add the destination marker
        map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

        // Move the camera to the destination only if the current location is not available
        if (lastLocation == null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, zoomLevel));
        }

        // Set a listener for map drag events
        map.setOnCameraMoveListener(() -> {
            // Check if the camera has been moved manually by the user
            if (!isCameraMoving) {
                isCameraMoving = true;
            }
        });

        // Check if map is not null before using it
        if (map != null) {
            requestLastLocation();
        }
    }



    private void requestLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Handle the case where permissions are not granted
            return;
        }
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Update the map with the last known location
                        lastLocation = location;
                        updateMapWithCurrentLocation(new LatLng(location.getLatitude(), location.getLongitude()));
                    }
                });
    }

    private void updateMapWithCurrentLocation(LatLng currentLatLng) {
        if (map != null) {
            // Clear previous markers
            map.clear();

            // Add the destination marker
            map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));

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
    private void updateMapWithDestination(LatLng destinationLatLng) {
        // Update the map with the destination marker
        if (map != null) {
            map.clear();
            map.addMarker(new MarkerOptions().position(destinationLatLng).title("Destination"));
            map.addMarker(new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("Current Location"));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(destinationLatLng, 16.0f));
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