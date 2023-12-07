package com.example.lorav4.Admin;// YourActivity.java

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.R;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Admin_transaction extends AppCompatActivity {

    private FirebaseDBHelper firebaseDBHelper;
    private Spinner spinnerUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_transaction);

        // Initialize FirebaseDBHelper
        firebaseDBHelper = new FirebaseDBHelper();

        // Initialize Spinner
        spinnerUsers = findViewById(R.id.spinnerUsers);

        // Populate spinner with data from Firebase
        populateSpinner();
    }

    private void populateSpinner() {
        firebaseDBHelper.getAllMobileNumbers(new FirebaseDBHelper.DataCallback() {
            @Override
            public void onDataReceived(DataSnapshot dataSnapshot) {
                // Handle the data snapshot and populate the spinner
                Iterable<DataSnapshot> data = dataSnapshot.getChildren();
                List<String> mobileNumbers = new ArrayList<>();

                for (DataSnapshot snapshot : data) {
                    // Assuming "mobile_number" is the key in your database
                    String mobileNumber = snapshot.child("mobileNumber").getValue(String.class);

                    // Exclude a specific user (modify the condition accordingly)
                    if (mobileNumber != null && !mobileNumber.equals("09000000000")) {
                        mobileNumbers.add(mobileNumber);
                    }
                }

                // Create an ArrayAdapter and set it to the spinner
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        Admin_transaction.this,
                        android.R.layout.simple_spinner_item,
                        mobileNumbers
                );

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerUsers.setAdapter(adapter);
            }
        });
    }
}
