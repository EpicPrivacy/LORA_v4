package com.example.lorav4.Driver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.lorav4.Change_password;
import com.example.lorav4.Login;
import com.example.lorav4.R;
import com.google.firebase.auth.FirebaseAuth;

public class Drivers_dashboard extends AppCompatActivity {

    private Button driver_btn_transaction,driver_btn_changePass,driver_btn_logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drivers_dashboard);

        driver_btn_transaction = findViewById(R.id.driver_btn_transaction);
        driver_btn_changePass = findViewById(R.id.driver_btn_changePass);
        driver_btn_logout = findViewById(R.id.driver_btn_logout);

        driver_btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Show a confirmation dialog before logging out
                new AlertDialog.Builder(Drivers_dashboard.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to log out?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Sign out the user from Firebase Authentication
                                FirebaseAuth.getInstance().signOut();

                                // Redirect to the login or main activity
                                startActivity(new Intent(Drivers_dashboard.this, Login.class));
                                finish(); // Finish the current activity to prevent going back with the back button
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });

        driver_btn_transaction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Drivers_dashboard.this, Drivers_transaction.class));

            }
        });
        driver_btn_changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(Drivers_dashboard.this, Change_password.class));

            }
        });
    }
}