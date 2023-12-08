package com.example.lorav4;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hbb20.CountryCodePicker;

import java.util.UUID;


public class Sign_up extends AppCompatActivity {

    private CountryCodePicker countryCodePicker;
    private EditText first_name, last_name, m_number, password, confirm_password;
    private Button termsConditionButton, clear, submit;
    private CheckBox agreeCheckbox;
    private FirebaseDatabase DB;
    private DatabaseReference reference;

    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private LatLng selectedLatLng;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private Spinner userSpinner;
    private boolean locationObtained = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        countryCodePicker = findViewById(R.id.login_countrycode);
        clear = findViewById(R.id.clear);
        submit = findViewById(R.id.submit);
        agreeCheckbox = findViewById(R.id.agree_checkbox);
        termsConditionButton = findViewById(R.id.tems_condition);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        m_number = findViewById(R.id.m_number);
        password = findViewById(R.id.password);
        confirm_password = findViewById(R.id.confirm_password);
        userSpinner = findViewById(R.id.spinner);

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.user_types, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        userSpinner.setAdapter(adapter);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this::onMapReady);
        }

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Create a location request
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(10000); // 10 seconds

        // Create a location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null || locationObtained) {
                    return;
                }

                for (Location location : locationResult.getLocations()) {
                    updateMap(new LatLng(location.getLatitude(), location.getLongitude()));
                    locationObtained = true;

                    // Stop location updates after obtaining the location
                    stopLocationUpdates();
                }
            }
        };

        termsConditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTermsAndConditionsDialog();
            }
        });

        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Clear the text in all EditText fields
                first_name.setText("");
                last_name.setText("");
                m_number.setText("");
                password.setText("");
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (agreeCheckbox.isChecked()) {
                    if (validateConfirmPassword()) {
                        DB = FirebaseDatabase.getInstance();
                        reference = DB.getReference().child("LORA");
                        register();
                    }
                } else {
                    Toast.makeText(Sign_up.this, "Please agree to the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                addMarkerToMap(latLng);
                selectedLatLng = latLng;
            }
        });

        // Check location permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                startLocationUpdates();
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        } else {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
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
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    private void addMarkerToMap(LatLng latLng) {
        mMap.clear(); // Clear existing markers

        MarkerOptions markerOptions = new MarkerOptions()
                .position(latLng)
                .title("Selected Location");
        mMap.addMarker(markerOptions);

        selectedLatLng = latLng;
    }

    private void updateMap(LatLng latLng) {
        mMap.clear();
        addMarkerToMap(latLng);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
    }
    private void showTermsAndConditionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions");
        builder.setMessage("\n" +
                "\n" +
                "Last Updated: [11/11/2023]\n" +
                "\n" +
                "Welcome to LORA!\n" +
                "\n" +
                "By accessing or using LORA, you agree to comply with and be bound by the following terms and conditions of use. If you do not agree to these terms, please do not use the app.\n" +
                "\n" +
                "1. **User Agreement:**\n" +
                "   - You must be at least 18 years old to use this app.\n" +
                "   - You are responsible for maintaining the confidentiality of your account and password.\n" +
                "\n" +
                "2. **Content:**\n" +
                "   - LORA reserves the right to modify, suspend, or discontinue any aspect of the app at any time.\n" +
                "   - Users are responsible for the content they post or share. Prohibited content includes but is not limited to offensive, abusive, or illegal materials.\n" +
                "\n" +
                "3. **Privacy:**\n" +
                "   - LORA respects your privacy. Please review our Privacy Policy for details on how we collect, use, and disclose information.\n" +
                "\n" +
                "4. **Intellectual Property:**\n" +
                "   - All content and materials in this app, including but not limited to text, graphics, logos, and images, are the property of LORA and are protected by intellectual property laws.\n" +
                "\n" +
                "5. **User Conduct:**\n" +
                "   - Users agree not to engage in any conduct that may harm the app or interfere with other users.\n" +
                "\n" +
                "6. **Termination:**\n" +
                "   - LORA reserves the right to terminate or suspend your account at any time without notice for any reason.\n" +
                "\n" +
                "7. **Disclaimer:**\n" +
                "   - The app is provided \"as is\" without any warranties.\n" +
                "   - LORA is not responsible for any damages resulting from the use of the app.\n" +
                "\n" +
                "8. **Governing Law:**\n" +
                "   - These terms are governed by and construed in accordance with the laws of Philippines.\n" +
                "\n" +
                "9. **Contact:**\n" +
                "   - For questions or concerns regarding these terms, please contact 09564001376.\n" +
                "\n" +
                "By using LORA, you acknowledge that you have read and understood these terms and conditions and agree to be bound by them.\n" +
                "\n" +
                "Logistics Routing Solution\n" +
                "56JG+C3 Los Baños, Laguna\n" +
                "09564001376"
        );

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private boolean validateFirstName(){
        String val = first_name.getEditableText().toString();

        if(val.isEmpty()){
            first_name.setError("Field cannot be empty");
            return false;
        }else {
            first_name.setError(null);

            return true;
        }
    }
    private boolean validateLastName(){
        String val = last_name.getEditableText().toString();

        if(val.isEmpty()){
            last_name.setError("Field cannot be empty");
            return false;
        }else {
            last_name.setError(null);
            return true;
        }
    }
    private boolean validateMNumber(){
        String val = m_number.getEditableText().toString();
        String NumberMatch = "^[+]?[0-9]{11}$";

        if(val.matches("m_number")){

        }
        if(val.isEmpty()){
            m_number.setError("Field cannot be empty");
            return false;
        } else if (val.length()!=11) {
            m_number.setError("Mobile number not valid");
            return false;
        }else if (!val.matches(NumberMatch)) {
            m_number.setError("Philippine number only");
            return false;
        }
        else {
            m_number.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        String val = password.getEditableText().toString();
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";

        if (val.isEmpty()) {
            password.setError("Password cannot be empty");
            return false;
        } else if (!val.matches(passwordPattern)) {
            password.setError("Password must contain at least 8 characters with one uppercase letter, one lowercase letter, one digit, and one special character");
            return false;
        } else {
            password.setError(null);
            return true;
        }
    }

    private boolean validateConfirmPassword() {
        String passwordVal = password.getEditableText().toString();
        String confirmPasswordVal = confirm_password.getEditableText().toString();

        if (!confirmPasswordVal.equals(passwordVal)) {
            confirm_password.setError("Passwords do not match");
            return false;
        } else {
            confirm_password.setError(null);
            return true;
        }
    }


    public void register() {
        if (!validateFirstName() || !validateLastName() || !validateMNumber() ||
                 !validatePassword() || !validateConfirmPassword()) {
            return;
        }


        String fname = first_name.getEditableText().toString();
        String lname = last_name.getText().toString();
        String mnumber = m_number.getText().toString();
        String pass = password.getText().toString();


        DatabaseReference userRef = DB.getReference().child("LORA").child(mnumber);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Mobile number already registered
                    m_number.setError("Mobile number already registered");
                } else {

                    String userType = userSpinner.getSelectedItem().toString();
                    // Generate a user ID (you can use a UUID or any other method)
                    String userId = UUID.randomUUID().toString();


                    // Mobile number is unique, proceed with registration
                    Helper helper = new Helper(userId, fname, lname, mnumber, pass, selectedLatLng.latitude, selectedLatLng.longitude, userType);
                    reference.child(mnumber).setValue(helper)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("Registration", "Registration successful");
                                        // Registration successful, proceed with OTP verification
                                        countryCodePicker.registerCarrierNumberEditText(m_number);
                                        if (!countryCodePicker.isValidFullNumber()) {
                                            m_number.setError("Phone number not valid");
                                            return;
                                        }

                                        // Start OTP verification
                                        Intent intent = new Intent(Sign_up.this, Verify_otp.class);
                                        intent.putExtra("m_number", countryCodePicker.getFullNumberWithPlus());
                                        intent.putExtra("user_id", userId); // Pass user ID to the next activity
                                        intent.putExtra("identity", userType);
                                        startActivity(intent);
                                    } else {
                                        Log.e("Registration", "Registration failed", task.getException());
                                        // Registration failed
                                        // Handle the error, if needed
                                    }
                                }
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle the error
            }
        });

    }
    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This method is called when the activity is being destroyed.
        // Release resources, unregister listeners, or perform cleanup tasks here.
    }
}